package tools.datasync.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlUtils {

    private final static Logger LOG = LoggerFactory.getLogger(SqlUtils.class);

    public static void runSQLScript(Connection conn, String path) {
	try {
	    run(conn, path);
	} catch (Exception e) {
	    throw (new RuntimeException(e));
	}
    }

    private static void run(Connection conn, String path)
	    throws FileNotFoundException, SQLException {
	InputStream in = new FileInputStream(new File(path));// SqlUtils.class.getClass().getResourceAsStream(path);
	Scanner sc = new Scanner(in);
	sc.useDelimiter(";");

	while (sc.hasNext()) {
	    String sql = sc.next();
	    sql = sql.trim();
	    boolean skipSql = isSkip(sql);
	    if (!skipSql) {
		LOG.debug(sql);
		Statement stmt = conn.createStatement();
		stmt.execute(sql);
		stmt.close();
	    }
	}
	sc.close();
	LOG.debug("Executed " + path + " successfully.");
    }

    private static final boolean isSkip(String sql) {
	if (StringUtils.isWhiteSpaceOnly(sql)) {
	    return true;
	}
	if (sql.startsWith("--")) {
	    return true;
	}
	return false;

    }
}
