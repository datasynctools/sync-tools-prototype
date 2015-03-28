package tools.datasync.dao.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.dao.ResultMapper;

public class JdbcSelectionHelper<T> {

    private static final Logger LOG = LoggerFactory
	    .getLogger(JdbcSelectionHelper.class);

    private DataSource dataSource;

    public JdbcSelectionHelper(DataSource dataSource) {
	this.dataSource = dataSource;
    }

    public T query(String query, ResultMapper<T> mapper, String dbName,
	    String entityName) {
	Connection connection = null;
	Statement statement = null;
	final ResultSet result;
	try {
	    connection = dataSource.getConnection();
	    statement = connection.createStatement();
	    LOG.debug(dbName + ": " + query);
	    result = statement.executeQuery(query);
	    return (mapper.map(result, entityName, new JdbcSelectionClosable(
		    connection, statement, result)));
	} catch (Exception e) {
	    LOG.warn(dbName + ": " + "result set error - select().", e);
	    throw new RuntimeException(e);
	}

	// documentation note: the mapper is responsible for closing the
	// connection

    }
}
