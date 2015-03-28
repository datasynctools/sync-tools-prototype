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

import tools.datasync.api.dao.GenericDao;
import tools.datasync.basic.logic.DbTableComparator;
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
public class CoreSeedIntegrationTest {

    private final static Logger LOG = LoggerFactory
	    .getLogger(CoreSeedIntegrationTest.class);

    private SyncOrchestrationManager syncOrchMgr;

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
	    LOG.error("Cannot load app enviornment", e);
	    if (appContext != null) {
		appContext.close();
	    }
	}

    }

    @Test
    public void positiveTest() {

	// for (int i = 1; i < 3; i++) {

	try {
	    LOG.info("first test...");
	    SyncSession syncSession = syncOrchMgr.createSession();

	    syncSession.doSync();
	    DbTableComparator comparator = new DbTableComparator(sourceDao,
		    targetDao);

	    comparator.compare("org.Contact");
	    comparator.compare("org.WorkHistory");
	    comparator.compare("org.ContactLink");
	    comparator.compare("seed.SyncState");

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

	setupDerbyProps();

	try {
	    Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	    throw (new RuntimeException("Could not load derby driver"));
	}

	Connection conn = null;
	try {
	    conn = DriverManager.getConnection("jdbc:derby:" + dbName
		    + ";create=true");
	} catch (SQLException e) {
	    Utils.closeQuietly(conn);
	    throw (new RuntimeException(e));
	}
	return (conn);

    }

}
