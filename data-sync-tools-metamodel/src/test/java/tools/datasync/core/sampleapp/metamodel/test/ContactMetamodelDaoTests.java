package tools.datasync.core.sampleapp.metamodel.test;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

import junit.framework.Assert;

import org.apache.commons.dbcp2.Utils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.PropertyConfigurator;
import org.apache.metamodel.UpdateableDataContext;
import org.apache.metamodel.jdbc.JdbcDataContext;
import org.junit.BeforeClass;

import tools.datasync.api.dao.SyncRecordFromT;
import tools.datasync.core.sampleapp.AbstractContactDaoTests;
import tools.datasync.core.sampleapp.dao.ContactDao;
import tools.datasync.core.sampleapp.dao.impl.metamodel.ContactMetamodelDao;
import tools.datasync.core.sampleapp.model.Contact;
import tools.datasync.core.sampleapp.sync.creators.SyncRecordFromContact;
import tools.datasync.utils.DefaultHashFromObject;

public class ContactMetamodelDaoTests extends AbstractContactDaoTests {

    static final String BASE_FILE_PATH = "derby-tmp";
    static final String DB_FILE_PATH = BASE_FILE_PATH + "/resources/test1.db";

    @BeforeClass
    public static void setupBefore() {
	PropertyConfigurator.configure("src/test/resources/log4j.properties");
	FileUtils.deleteQuietly(new File(DB_FILE_PATH));
	Connection conn = createConnection(DB_FILE_PATH, true);
	UpdateableDataContext updateableContext = new JdbcDataContext(conn);

	ContactTableCreator.createDb(updateableContext);
	SyncStateTableCreator.createDb(updateableContext);
    }

    public ContactDao setupTestInsertAndSelect() {
	Connection conn = createConnection(DB_FILE_PATH, true);
	UpdateableDataContext updateableContext = new JdbcDataContext(conn);

	SyncRecordFromT<Contact> syncRecordCreator = new SyncRecordFromContact(
		new DefaultHashFromObject(), UUID.randomUUID().toString());

	ContactDao contactDao = new ContactMetamodelDao(updateableContext,
		syncRecordCreator);

	Assert.assertEquals(0, contactDao.getContacts().size());

	return contactDao;
    }

    private static void cleanupDir() {

	File tmpDir = new File(".", "derby-tmp");
	try {
	    FileUtils.forceMkdir(tmpDir);
	} catch (IOException e) {
	    e.printStackTrace();
	    throw (new RuntimeException(
		    "Cannot create temp directory for derby"));
	}
    }

    private static void setupDerbyProps() {
	// doc: http://docs.oracle.com/javadb/10.10.1.2/ref/rrefproper46141.html
	System.setProperty("derby.locks.waitTimeout", "5");
	System.setProperty("derby.locks.deadlockTimeout", "4");

    }

    private static Connection createConnection(String dbName, boolean create) {

	cleanupDir();

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

}
