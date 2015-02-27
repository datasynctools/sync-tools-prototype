package tools.datasync.core.sampleapp;

import java.sql.Connection;

public class SampleAppDataCreatorA {

    public static void createDb(Connection conn) {
	// TODO Remove hard coding of sql statement
	String path = "src/test/resources/populate_database_peerA.sql";
	try {
	    SqlUtils.runSQLScript(conn, path);
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

}
