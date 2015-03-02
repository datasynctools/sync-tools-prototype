package tools.datasync.basic.dao;

import java.io.Closeable;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.basic.model.JSON;

public class JdbcJsonIterator implements Iterator<JSON> {

    private static final Logger LOG = LoggerFactory
	    .getLogger(JdbcJsonIterator.class);

    private final ResultSet result;
    private String entityName;
    private List<String> primaryKeyColumns;
    private final Closeable closable;

    private boolean hasMore = false;

    public JdbcJsonIterator(final ResultSet result, String entityName,
	    List<String> primaryKeyColumns, final Closeable closable) {
	this.result = result;
	this.entityName = entityName;
	this.primaryKeyColumns = primaryKeyColumns;
	this.closable = closable;
    }

    public boolean hasNext() {
	try {
	    hasMore = result.next();
	    return hasMore;
	} catch (SQLException e) {

	    throw (new RuntimeException(e));
	    // logger.error(dbName + ": "
	    // + "result set error - hasNext().", e);
	    // return false;
	}
    }

    private void addPrimaryKeys(StringBuffer sbPrimaryKey) throws SQLException {
	for (String pkColumn : primaryKeyColumns) {
	    String key = result.getString(pkColumn);
	    sbPrimaryKey.append(key);
	    sbPrimaryKey.append("->");
	}
    }

    private void addObjects(JSON json, int count) throws SQLException {
	for (int index = 1; index <= count; index++) {
	    String columnName = result.getMetaData().getColumnName(index);
	    Object value = result.getObject(index);

	    json.set(columnName.toUpperCase(), value);
	}
    }

    private JSON nextLogic() throws SQLException {
	JSON json = new JSON(entityName);

	StringBuffer sbPrimaryKey = new StringBuffer();
	addPrimaryKeys(sbPrimaryKey);
	if (sbPrimaryKey.length() > 2) {
	    sbPrimaryKey.setLength(sbPrimaryKey.length() - 2);
	}
	json.setCalculatedPrimaryKey(sbPrimaryKey.toString());
	// logger.debug("ResultSet.next() - calculated primary key: "
	// + json.getCalculatedPrimaryKey());

	int count = result.getMetaData().getColumnCount();
	addObjects(json, count);
	count++;
	// logger.debug(dbName + ": ResultSet.next() - returning "
	// + entityName + " - " + json);
	return json;
    }

    public JSON next() {
	try {

	    return (nextLogic());

	} catch (SQLException e) {
	    // logger.warn(dbName + ": result set error - next().", e);
	    throw new RuntimeException(e);
	} finally {
	    try {
		if (!hasMore) {
		    // logger.debug(dbName
		    // + ": selectAll() - closing resultset");
		    closable.close();
		}
	    } catch (IOException e) {
		LOG.error("Error closing on last message", e);
		throw (new RuntimeException(e));
	    }
	}
    }

    public void remove() {
	// Do nothing
    }

}
