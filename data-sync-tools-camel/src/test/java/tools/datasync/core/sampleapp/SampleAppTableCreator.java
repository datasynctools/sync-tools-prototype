package tools.datasync.core.sampleapp;

import java.sql.Connection;

public class SampleAppTableCreator {

    public static void createDb(Connection conn) {
	// TODO Remove hard coding of sql statement
	String path = "src/test/resources/create_table_model.sql";
	try {
	    SqlUtils.runSQLScript(conn, path);
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

}
