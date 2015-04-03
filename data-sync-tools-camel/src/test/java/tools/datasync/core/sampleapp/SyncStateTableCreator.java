package tools.datasync.core.sampleapp;

import java.sql.Connection;

import tools.datasync.utils.SqlUtils;

public class SyncStateTableCreator {

    public static void createDb(Connection conn, String path) {
	try {
	    SqlUtils.runSQLScript(conn, path);
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

}
