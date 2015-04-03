package tools.datasync.core.sampleapp;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbcp2.Utils;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import tools.datasync.api.dao.GenericDao;
import tools.datasync.pump.SyncOrchestrationManager;
import tools.datasync.pump.SyncSession;
import tools.datasync.utils.DbTableComparator;
import tools.datasync.utils.SqlUtils;

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
public class CamelSeedIntegrationTests {

    private final static Logger LOG = LoggerFactory
	    .getLogger(CamelSeedIntegrationTests.class);

    private SyncOrchestrationManager syncOrchMgr;

    private GenericDao sourceDao = null;
    private GenericDao targetDao = null;

    private static String pref = "src/test/resources/";

    private static void createDatabases() {
	FileUtils.deleteQuietly(new File("db-A"));
	FileUtils.deleteQuietly(new File("db-B"));

	Connection conn = createConnection("db-A", true);
	SqlUtils.runSQLScript(conn, pref + "create_table_framework.sql");
	SqlUtils.runSQLScript(conn, pref + "create_table_model.sql");

	conn = createConnection("db-B", true);
	SqlUtils.runSQLScript(conn, pref + "create_table_framework.sql");
	SqlUtils.runSQLScript(conn, pref + "create_table_model.sql");
    }

    @BeforeClass
    public static void staticSetup() {
	createDatabases();
    }

    private void positiveTestSetup() {
	Connection conn = createConnection("db-A", true);
	SqlUtils.runSQLScript(conn, pref + "clear_database_model.sql");
	SqlUtils.runSQLScript(conn, pref
		+ "populate_positive_database_peerA.sql");
	conn = createConnection("db-B", true);
	SqlUtils.runSQLScript(conn, pref + "clear_database_model.sql");
	SqlUtils.runSQLScript(conn, pref
		+ "populate_positive_database_peerA.sql");
    }

    @Test
    public void positiveTest() {

	// for (int i = 1; i < 3; i++) {

	positiveTestSetup();

	loadTestObjs("test-beans.xml");

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

    private void loadTestObjs(String beansFile) {

	ClassPathXmlApplicationContext appContext = null;
	try {
	    appContext = new ClassPathXmlApplicationContext(beansFile);

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

    private void stopTestSetup() {
	Connection conn = createConnection("db-A", true);
	SqlUtils.runSQLScript(conn, pref + "clear_database_model.sql");
	SqlUtils.runSQLScript(conn, pref + "populate_stop_database_peerA.sql");
	conn = createConnection("db-B", true);
	SqlUtils.runSQLScript(conn, pref + "clear_database_model.sql");
	SqlUtils.runSQLScript(conn, pref + "populate_stop_database_peerB.sql");
    }

    private void stopTestCheckResultsA() throws Exception {
	Connection conn = createConnection("db-A", true);
	ResultSet rs = conn.createStatement().executeQuery(
		"select * from org.Contact");

	Assert.assertTrue(rs.next() == true);
	Assert.assertTrue(rs.getString("FirstName").equals("John"));
	Assert.assertTrue(rs.next() == false);
    }

    private void stopTestCheckResultsB() throws Exception {
	Connection conn = createConnection("db-B", true);
	ResultSet rs = conn.createStatement().executeQuery(
		"select * from org.Contact");

	Assert.assertTrue(rs.next() == true);
	Assert.assertTrue("value [" + rs.getString("FirstName")
		+ "] is not what it's expected to be", rs
		.getString("FirstName").equals("Jill"));
	Assert.assertTrue(rs.next() == false);
    }

    private void stopTestCheckResults() {
	try {

	    // A basic proof that there are no changes were made from the
	    // original tables (showing that the process stopped)
	    stopTestCheckResultsA();

	    stopTestCheckResultsB();

	} catch (Exception e) {
	    throw (new RuntimeException(e));
	}
    }

    @Test
    public void stopTest() {

	stopTestSetup();

	loadTestObjs("test-stop-beans.xml");

	try {
	    LOG.info("first test...");
	    SyncSession syncSession = syncOrchMgr.createSession();

	    syncSession.doSync();

	    stopTestCheckResults();

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
