package tools.datasync.core.sampleapp.jdbc.test;

import java.sql.Connection;

import tools.datasync.basic.util.SqlUtils;

public class SyncStateTableCreator {

    public static void createDb(Connection conn) {
	String path = "src/test/resources/create_table_framework.sql";
	try {
	    SqlUtils.runSQLScript(conn, path);
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

}
