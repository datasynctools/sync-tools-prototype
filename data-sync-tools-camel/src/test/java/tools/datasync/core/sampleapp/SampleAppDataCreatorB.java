package tools.datasync.core.sampleapp;

import java.sql.Connection;

import tools.datasync.utils.SqlUtils;

public class SampleAppDataCreatorB {

    public static void createDb(Connection conn) {
	// TODO Remove hard coding of sql statement
	String path = "src/test/resources/populate_database_peerB.sql";
	try {
	    SqlUtils.runSQLScript(conn, path);
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

}
