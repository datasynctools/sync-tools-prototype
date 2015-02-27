package tools.datasync.core.sampleapp;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.dbcp2.Utils;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import tools.datasync.basic.dao.GenericDao;
import tools.datasync.basic.logic.DbTableComparator;
import tools.datasync.basic.model.Ids;
import tools.datasync.basic.sync.SyncOrchestrationManager;
import tools.datasync.basic.sync.SyncSession;

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
public class SeedIntegrationTest {

    private final static Logger LOG = LoggerFactory
	    .getLogger(SeedIntegrationTest.class);

    private SyncOrchestrationManager syncOrchMgr;
    // private SyncPumpFactory pumpFactoryA;
    // private SyncPumpFactory pumpFactoryB;
    // private AtomicBoolean stopper = new AtomicBoolean(false);

    private GenericDao sourceDao = null;
    private GenericDao targetDao = null;

    private void createDatabases() {
	FileUtils.deleteQuietly(new File("db-A"));
	FileUtils.deleteQuietly(new File("db-B"));

	Connection conn = createConnection("db-A", true);
	SyncStateTableCreator.createDb(conn);
	SampleAppTableCreator.createDb(conn);
	SampleAppDataCreatorA.createDb(conn);

	conn = createConnection("db-B", true);
	SyncStateTableCreator.createDb(conn);
	SampleAppTableCreator.createDb(conn);
	SampleAppDataCreatorB.createDb(conn);
    }

    @Before
    public void init() {

	createDatabases();

	ClassPathXmlApplicationContext appContext = null;
	try {
	    appContext = new ClassPathXmlApplicationContext("test-beans.xml");

	    syncOrchMgr = appContext.getBean("syncOrchMgr",
		    SyncOrchestrationManager.class);

	    sourceDao = appContext.getBean("sourceDao", GenericDao.class);
	    targetDao = appContext.getBean("targetDao", GenericDao.class);

	} catch (Exception e) {
	    appContext.close();
	}

    }

    @Test
    public void firstTest() {

	// for (int i = 1; i < 3; i++) {

	try {
	    LOG.info("first test...");
	    SyncSession syncSession = syncOrchMgr.createSession();

	    syncSession.doSync();
	    DbTableComparator comparator = new DbTableComparator(sourceDao,
		    targetDao);

	    comparator.compare(Ids.Table.CONTACT);
	    comparator.compare(Ids.Table.WORK_HISTORY);
	    comparator.compare(Ids.Table.CONTACT_LINK);
	    comparator.compare(Ids.Table.SYNC_STATE);

	} catch (Exception ex) {
	    ex.printStackTrace();
	    Assert.fail("Exception: " + ex.getMessage());
	}

	// init();
	// }
    }

    private static void setupDerbyProps() {
	// doc: http://docs.oracle.com/javadb/10.10.1.2/ref/rrefproper46141.html
	System.setProperty("derby.locks.waitTimeout", "5");
	System.setProperty("derby.locks.deadlockTimeout", "4");

    }

    private static Connection createConnection(String dbName, boolean create) {

	// cleanupDir();

	setupDerbyProps();

	try {
	    Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	    throw (new RuntimeException("Could not load derby driver"));
	}

	Connection conn = null;
	try {
	    // jdbc:derby:derby/TrustyTask;create=true
	    conn = DriverManager.getConnection("jdbc:derby:" + dbName
		    + ";create=true");
	} catch (SQLException e) {
	    Utils.closeQuietly(conn);
	    throw (new RuntimeException(e));
	}
	return (conn);

    }

    // public void init(){
    // LOG.info("init...");
    // SyncPeer syncPeerA = new SyncPeer("A");
    // SyncPeer syncPeerB = new SyncPeer("B");
    //
    // String dbA = "db-A";
    // String dbB = "db-B";
    // EmbeddedDataSource dataSourceA = new EmbeddedDataSource();
    // dataSourceA.setDatabaseName(dbA);
    // EmbeddedDataSource dataSourceB = new EmbeddedDataSource();
    // dataSourceB.setDatabaseName(dbB);
    //
    // CountDownLatch beginSenderLatch = new CountDownLatch(2);
    //
    // BlockingQueue<String> a2bQueue = new LinkedBlockingQueue<String>();
    // BlockingQueue<String> b2aQueue = new LinkedBlockingQueue<String>();
    //
    // JvmSyncPeerParms peerMe = new JvmSyncPeerParms(syncPeerA, a2bQueue);
    // JvmSyncPeerParms peerOther = new JvmSyncPeerParms(syncPeerB,
    // b2aQueue);
    // JvmSyncPair pairA = new JvmSyncPair(peerMe, peerOther);
    // JvmSyncPair pairB = new JvmSyncPair(peerOther, peerMe);
    //
    // sourceDao = new GenericJDBCDao(dataSourceA, dbA,
    // new SampleAppTableNameGetter());
    // // sourceDao.setDataSource(dataSourceA, dbA);
    //
    // targetDao = new GenericJDBCDao(dataSourceB, dbB,
    // new SampleAppTableNameGetter());
    // // sourceDao.setDataSource(dataSourceB, dbB);
    //
    // SeedConsumerFactory seedConsumerFactory = new DbSeedConsumerFactory(
    // new SampleAppTableNameGetter());
    //
    // GenericDaoPair daoPairA = new GenericDaoPair(sourceDao, targetDao,
    // seedConsumerFactory);
    //
    // GenericDaoPair daoPairB = new GenericDaoPair(targetDao, sourceDao,
    // seedConsumerFactory);
    //
    // JvmSyncConcurArgs concurArgs = new JvmSyncConcurArgs(stopper,
    // beginSenderLatch);
    // // String[] tables = { Ids.Table.CONTACT, Ids.Table.WORK_HISTORY,
    // // Ids.Table.CONTACT_LINK };
    // // String[] tables = { Ids.Table.CONTACT };
    // List<String> tables = new ArrayList<String>();
    // tables.add(Ids.Table.CONTACT);
    // tables.add(Ids.Table.WORK_HISTORY);
    // tables.add(Ids.Table.CONTACT_LINK);
    //
    // SyncStateInitializer syncStateInitializerA = new
    // JDBCSyncStateInitializer(
    // tables, new SampleSyncIdGetter(), sourceDao);
    // SyncStateInitializer syncStateInitializerB = new
    // JDBCSyncStateInitializer(
    // tables, new SampleSyncIdGetter(), targetDao);
    //
    // pumpFactoryA = new JvmSyncPumpFactory(pairA, daoPairA,
    // syncStateInitializerA, concurArgs);
    // pumpFactoryB = new JvmSyncPumpFactory(pairB, daoPairB,
    // syncStateInitializerB, concurArgs);
    // syncOrchMgr = new SyncOrchestrationManager(pumpFactoryA,
    // pumpFactoryB);
    // }

}
