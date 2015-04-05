package tools.datasync.core.sampleapp.jdbc.test;

import java.sql.Connection;

import tools.datasync.utils.SqlUtils;

public class ContactTableCreator {

    public static void createDb(Connection conn) {
	String path = "src/main/resources/META-INF/sampleCreate.sql";
	try {
	    SqlUtils.runSQLScript(conn, path);
	} catch (Exception e) {
	    throw (new RuntimeException(e));
	}
    }

}
