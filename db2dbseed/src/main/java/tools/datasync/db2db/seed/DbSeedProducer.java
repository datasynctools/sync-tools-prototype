/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * @author  Upendra Jariya
 * @sponsor Douglas Johnson
 * @version 1.0
 * @since   2014-11-10
 */
package tools.datasync.db2db.seed;

import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;

import tools.datasync.db2db.dao.GenericDao;
import tools.datasync.db2db.model.Ids;
import tools.datasync.db2db.model.JSON;
import tools.datasync.db2db.model.SeedRecord;
import tools.datasync.db2db.sync.SyncDataHandler;
import tools.datasync.db2db.sync.SyncManager;
import tools.datasync.db2db.sync.SyncPeer;
import tools.datasync.db2db.util.ExceptionHandler;
import tools.datasync.db2db.util.HashGenerator;
import tools.datasync.db2db.util.JSONMapperBean;

public class DbSeedProducer implements SeedProducer, Runnable {

	@Autowired
	GenericDao genericDao;
	@Autowired
	SyncDataHandler dataHandler;
	@Autowired
	ExceptionHandler exceptionHandler;
	@Autowired
	HashGenerator hashGenerator;
	@Autowired
	JSONMapperBean jsonMapper;
	SyncPeer me = null;

	Logger logger = Logger.getLogger(DbSeedProducer.class.getName());
	boolean stop = false;

	public DbSeedProducer(SyncPeer peer) {
		this.me = peer;
	}

	public boolean publish(JSON record) throws InterruptedException {

		if (stop) {
			throw new InterruptedException("");
		}
		SeedRecord seed = null;
		try {
			String entityId = Ids.EntityId.get(record.getEntity());
			String recordId = String.valueOf(record.get(Ids.KeyColumn.get(record.getEntity())));
			String recordJson = jsonMapper.writeValueAsString(record);
			String recordHash = hashGenerator.generate(recordJson);
			String origin = me.getPeerId();
			seed = new SeedRecord(entityId, recordId, recordHash, recordJson, origin);
			logger.finest("generated seed record: " + seed);
		} catch (IOException e) {
			exceptionHandler.handle(e, Level.WARNING, "Error while JSON Serialization", record);
		}

		try {
			if (seed != null) {
				logger.finer("Publishing seed record: " + seed);
				dataHandler.seedOut(seed);
				return true;
			}
		} catch (Exception ex) {
			exceptionHandler.handle(ex, Level.WARNING, "Error while sending record to peer", record);
		}

		return false;
	}

	public void run() {
		try {

			Iterator<JSON> contactIterator = genericDao.selectAll(Ids.Table.CONTACT);
			while (contactIterator.hasNext()) {
				this.publish(contactIterator.next());
			}

			Iterator<JSON> workHistoryIterator = genericDao.selectAll(Ids.Table.WORK_HISTORY);
			while (workHistoryIterator.hasNext()) {
				this.publish(workHistoryIterator.next());
			}

			Iterator<JSON> contactLinkIterator = genericDao.selectAll(Ids.Table.CONTACT_LINK);
			while (contactLinkIterator.hasNext()) {
				this.publish(contactLinkIterator.next());
			}

		} catch (InterruptedException interrupt) {
			exceptionHandler.handle(interrupt, Level.SEVERE, "Seed Producer is interrupted by another thread.");
		}
	}

	public void stop() {
		logger.info("Stopping DB producer thread.");
		this.stop = true;
	}

}
