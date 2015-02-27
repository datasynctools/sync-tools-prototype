package tools.datasync.core.sampleapp.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonJdbcHelper {

    private final static Logger LOG = LoggerFactory
	    .getLogger(CommonJdbcHelper.class);

    public final static void processSqlMutation(DataSource dataSource,
	    List<String> sqlList) {
	Connection connection = null;
	Statement statement = null;
	try {
	    connection = dataSource.getConnection();
	    statement = connection.createStatement();
	    for (String sql : sqlList) {
		processSqlMutation(sql, statement);
	    }
	    connection.commit();
	} catch (Exception e) {
	    String msg = "SQL Exception: " + e.getMessage();
	    LOG.error(msg, e);
	    throw (new RuntimeException(msg, e));
	} finally {
	    Utils.closeQuietly(statement);
	    Utils.closeQuietly(connection);
	}

    }

    public final static void processSqlSelect(DataSource dataSource,
	    String sql, CreatorFromResultSet<?> resultSetCreator) {
	Connection connection = null;
	Statement statement = null;
	try {
	    connection = dataSource.getConnection();
	    connection.setAutoCommit(false);
	    statement = connection.createStatement();

	    ResultSet resultSet = processSqlQuery(sql, statement);

	    resultSetCreator.createAndSetValue(resultSet);

	} catch (Exception e) {
	    String msg = "SQL Exception: " + e.getMessage();
	    LOG.error(msg, e);
	    throw (new RuntimeException(msg, e));
	} finally {
	    Utils.closeQuietly(statement);
	    Utils.closeQuietly(connection);
	}

    }

    private final static void processSqlMutation(String sql, Statement statement)
	    throws Exception {

	try {
	    statement.execute(sql);
	} catch (SQLException e) {
	    String msg = "SQL Exception on sql [" + sql + "], message: "
		    + e.getMessage();
	    LOG.error(msg, e);
	    throw (e);
	}

    }

    private final static ResultSet processSqlQuery(String sql,
	    Statement statement) throws Exception {

	try {
	    ResultSet resultSet = statement.executeQuery(sql);
	    return (resultSet);
	} catch (SQLException e) {
	    String msg = "SQL Exception on sql [" + sql + "], message: "
		    + e.getMessage();
	    LOG.error(msg, e);
	    throw (e);
	}

    }
}
