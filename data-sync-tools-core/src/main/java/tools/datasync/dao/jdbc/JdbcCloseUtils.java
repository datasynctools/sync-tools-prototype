package tools.datasync.dao.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JdbcCloseUtils {

    public static void closeRuntimeException(Connection connection,
	    Statement statement, ResultSet result) {
	try {
	    close(connection, statement, result);
	} catch (Exception e) {
	    throw (new RuntimeException(e));
	}
    }

    static private void close(Connection connection, Statement statement,
	    ResultSet result) throws SQLException {
	if (result != null) {
	    result.close();
	}
	if (statement != null) {
	    statement.close();
	}
	if (connection != null) {
	    connection.close();
	}
    }

    public static void closeRuntimeException(Connection connection,
	    Statement statement) {
	try {
	    close(connection, statement);
	} catch (Exception e) {
	    throw (new RuntimeException(e));
	}
    }

    static private void close(Connection connection, Statement statement)
	    throws SQLException {
	if (statement != null) {
	    statement.close();
	}
	if (connection != null) {
	    connection.close();
	}
    }
}
