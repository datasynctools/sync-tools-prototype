package tools.datasync.core.sampleapp;

import java.sql.Connection;

import tools.datasync.utils.SqlUtils;

public class SyncStateTableCreator {

    public static void createDb(Connection conn) {
	// TODO Remove hard coding of sql statement
	String path = "src/test/resources/create_table_framework.sql";
	try {
	    SqlUtils.runSQLScript(conn, path);
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

}
