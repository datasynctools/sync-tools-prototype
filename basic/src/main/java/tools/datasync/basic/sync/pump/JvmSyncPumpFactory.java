/**
 * 
 */
package tools.datasync.basic.sync.pump;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sql.DataSource;

import org.apache.derby.jdbc.EmbeddedDataSource;
import org.apache.log4j.Logger;

import tools.datasync.basic.dao.GenericDao;
import tools.datasync.basic.dao.GenericJDBCDao;
import tools.datasync.basic.logic.ConflictResolver;
import tools.datasync.basic.logic.InitiatorWinsConflictResolver;
import tools.datasync.basic.seed.DbSeedConsumer;
import tools.datasync.basic.seed.DbSeedProducer;
import tools.datasync.basic.sync.SyncPeer;
import tools.datasync.basic.util.StringUtils;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @author Upendra Jariya
 * @sponsor Douglas Johnson
 * @version 1.0
 * @since 29-Nov-2014
 */
public class JvmSyncPumpFactory implements SyncPumpFactory {

	private AtomicBoolean stopper = new AtomicBoolean(false);

	SyncPeer syncPeerMe = null;
	SyncPeer syncPeerOther = null;
	BlockingQueue<String> queueA2B = null;
	BlockingQueue<String> queueB2A = null;

	GenericJDBCDao sourceDao = null;
	GenericJDBCDao targetDao = null;

	// TODO: initiate single beginSeedLatch with count = 2
	CountDownLatch ackPairReceiverLatchA = new CountDownLatch(1);
	CountDownLatch ackPairReceiverLatchB = new CountDownLatch(1);

	CountDownLatch ackPeerSenderLatchA = new CountDownLatch(1);
	CountDownLatch ackPeerSenderLatchB = new CountDownLatch(1);
	
	CountDownLatch beginSenderLatch = null;

	Logger logger = Logger.getLogger(JvmSyncPumpFactory.class.getName());

	/**
	 * @param syncPeerMe
	 * @param syncPeerOther
	 */
	public JvmSyncPumpFactory(SyncPeer syncPeerMe, SyncPeer syncPeerOther, BlockingQueue<String> queueA2B, BlockingQueue<String> queueB2A,
			AtomicBoolean stopper, CountDownLatch beginSenderLatch) {
		super();
		this.syncPeerMe = syncPeerMe;
		this.syncPeerOther = syncPeerOther;
		this.queueA2B = queueA2B;
		this.queueB2A = queueB2A;
		this.stopper = stopper;
		this.beginSenderLatch = beginSenderLatch;
	}

	public SyncPump getInstance(PeerMode peerMode) throws InstantiationException {

		try {
			String sourceDb = "";
			String targetDb = "";
			CountDownLatch ackPairReceiverLatch;
			CountDownLatch ackPeerSenderLatch;

			// CountDownLatch receiveBeginSeedLatch;
			BlockingQueue<String> queue = null;
			ConflictResolver conflictResolver = null;
			if (PeerMode.A2B.equals(peerMode)) {
				queue = queueB2A;
				sourceDb = "db-A";
				targetDb = "db-B";
				ackPairReceiverLatch = ackPairReceiverLatchA;
				ackPeerSenderLatch = ackPeerSenderLatchB;
				// receiveBeginSeedLatch = ackPairReceiverLatchA;
				conflictResolver = new InitiatorWinsConflictResolver(false);
			} else {
				queue = queueA2B;
				sourceDb = "db-B";
				targetDb = "db-A";
				ackPairReceiverLatch = ackPairReceiverLatchB;
				ackPeerSenderLatch = ackPeerSenderLatchA;
				// receiveBeginSeedLatch = ackPairReceiverLatchB;
				conflictResolver = new InitiatorWinsConflictResolver(true);
			}

			JvmSyncPumpSender sender = new JvmSyncPumpSender(queue, stopper);
			{
				DataSource sourceDataSource = createDataSource(sourceDb, true);
				sourceDao = new GenericJDBCDao();
				sourceDao.setDataSource(sourceDataSource, sourceDb);
				prepareDatabase(sourceDataSource);

				DbSeedProducer seedProducer = new DbSeedProducer();
				seedProducer.setGenericDao(sourceDao);

				sender.setSeedProducer(seedProducer);
				sender.setAckPairReceiverLatch(ackPairReceiverLatch);
				sender.setAckPeerSenderLatch(ackPeerSenderLatch);
				sender.setBeginSenderLatch(beginSenderLatch);
			}

			JvmSyncPumpReceiver receiver = new JvmSyncPumpReceiver(queue, stopper);
			{
				DataSource targetDataSource = createDataSource(targetDb, true);
				targetDao = new GenericJDBCDao();
				targetDao.setDataSource(targetDataSource, targetDb);

				DbSeedConsumer seedConsumer = new DbSeedConsumer(conflictResolver);
				seedConsumer.setGenericDao(targetDao);

				receiver.setSeedConsumer(seedConsumer);
				receiver.setAckPairReceiverLatch(ackPairReceiverLatch);
				receiver.setAckPeerSenderLatch(ackPeerSenderLatch);
			}
			return new JvmSyncPump(peerMode, sender, receiver);

		} catch (Exception ex) {
			logger.error("Cannot instantiate JvmSyncPump", ex);
			throw new InstantiationException(ex.getMessage());
		}
	}

	/* ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
	/* Create and populate the databases before trial */
	/* ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */

	// creates the Apache Derby data source for given DB name
	private DataSource createDataSource(String dbname, boolean create) throws Exception {
		EmbeddedDataSource ds = new EmbeddedDataSource();
		ds.setDatabaseName(dbname);
		if (create) {
			ds.setCreateDatabase("create");
		}
		return ds;
	}

	private void prepareDatabase(DataSource dataSource) throws IOException, SQLException {
		try {
			Connection con = dataSource.getConnection();

			logger.info("Creating framework database...");
			runSQLScript(con, "/script/create_table_framework.sql");

			logger.info("Creating model database...");
			runSQLScript(con, "/script/create_table_model.sql");

			logger.info("Populating model database for Peer " + syncPeerMe.getPeerName());
			runSQLScript(con, "/script/populate_database_peer" + syncPeerMe.getPeerName() + ".sql");

			con.commit();
			con.close();

		} catch (IOException | SQLException e) {
			logger.error("Cannot prepare database." + e);
			throw e;
		}
	}

	private void runSQLScript(Connection con, String path) throws IOException, SQLException {
		InputStream in = this.getClass().getResourceAsStream(path);
		Scanner sc = new Scanner(in);
		sc.useDelimiter(";");

		while (sc.hasNext()) {
			String sql = sc.next();
			sql = sql.trim();
			if (StringUtils.isWhiteSpaceOnly(sql)) {
				continue;
			}
			if (sql.startsWith("--")) {
				continue;
			}
			logger.info(sql);
			Statement stmt = con.createStatement();
			stmt.execute(sql);
			stmt.close();
		}
		sc.close();
		logger.info("Executed " + path + " successfully.");
	}

	public GenericDao getSourceDao() {
		return sourceDao;
	}

	public GenericDao getTargetDao() {
		return targetDao;
	}

}
