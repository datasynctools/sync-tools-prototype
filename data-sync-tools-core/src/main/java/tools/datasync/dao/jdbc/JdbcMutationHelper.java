package tools.datasync.dao.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.api.msg.SyncEntityMessage;
import tools.datasync.utils.SqlGenUtil;

public class JdbcMutationHelper {

    private static final Logger LOG = LoggerFactory
	    .getLogger(JdbcMutationHelper.class);

    private DataSource dataSource;
    private SqlCreator insertCreator;
    private SqlCreator updateCreator;

    public JdbcMutationHelper(DataSource dataSource, SqlCreator insertCreator,
	    SqlCreator updateCreator) {
	this.dataSource = dataSource;
	this.insertCreator = insertCreator;
	this.updateCreator = updateCreator;
    }

    public void saveOrUpdate(String dbName, String entityName,
	    SyncEntityMessage syncEntityMsg, String keyColumn) throws SQLException {
	Connection connection = null;
	Statement statement = null;

	try {
	    connection = dataSource.getConnection();
	    statement = connection.createStatement();
	    executeInsert(statement, entityName, syncEntityMsg, keyColumn);

	} catch (SQLException ex) {
	    // May be primary key violation, try update statement...
	    if (SqlGenUtil.isConstraintViolation(ex)) {

		executeUpdate(statement, entityName, syncEntityMsg, keyColumn);

	    } else {
		LOG.error(dbName + ": Failed to insert record", ex);
		throw ex;
	    }
	} finally {

	    JdbcCloseUtils.closeRuntimeException(connection, statement);

	}
    }

    private void executeInsert(Statement statement, String entityName,
	    SyncEntityMessage syncEntityMsg, String keyColumn) throws SQLException {
	String sql = insertCreator.createSQL(entityName, syncEntityMsg, keyColumn);
	LOG.debug(sql + ": saveOrUpdate() - " + sql);
	statement.execute(sql);
    }

    private void executeUpdate(Statement statement, String entityName,
	    SyncEntityMessage syncEntityMsg, String keyColumn) throws SQLException {
	String sql = updateCreator.createSQL(entityName, syncEntityMsg, keyColumn);
	LOG.debug(sql + ": saveOrUpdate() - " + sql);
	statement.execute(sql);
    }

}
