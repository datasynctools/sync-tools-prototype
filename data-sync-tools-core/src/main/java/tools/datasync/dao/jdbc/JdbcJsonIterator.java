package tools.datasync.dao.jdbc;

import java.io.Closeable;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.api.msg.SyncEntityMessage;
import tools.datasync.model.DefaultSyncEntityMessage;

public class JdbcJsonIterator implements Iterator<SyncEntityMessage> {

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
	}
    }

    private void addPrimaryKeys(StringBuffer sbPrimaryKey) throws SQLException {
	for (String pkColumn : primaryKeyColumns) {
	    String key = result.getString(pkColumn);
	    sbPrimaryKey.append(key);
	    sbPrimaryKey.append("->");
	}
    }

    private void addObjects(SyncEntityMessage syncEntityMsg, int count)
	    throws SQLException {
	for (int index = 1; index <= count; index++) {
	    String columnName = result.getMetaData().getColumnName(index);
	    Object value = result.getObject(index);

	    syncEntityMsg.set(columnName.toUpperCase(), value);
	}
    }

    private SyncEntityMessage nextLogic() throws SQLException {
	SyncEntityMessage syncEntityMsg = new DefaultSyncEntityMessage();
	syncEntityMsg.setEntity(entityName);

	StringBuffer sbPrimaryKey = new StringBuffer();
	addPrimaryKeys(sbPrimaryKey);
	if (sbPrimaryKey.length() > 2) {
	    sbPrimaryKey.setLength(sbPrimaryKey.length() - 2);
	}
	syncEntityMsg.setCalculatedPrimaryKey(sbPrimaryKey.toString());

	int count = result.getMetaData().getColumnCount();
	addObjects(syncEntityMsg, count);
	count++;
	return syncEntityMsg;
    }

    public SyncEntityMessage next() {
	try {

	    return (nextLogic());

	} catch (SQLException e) {
	    throw new RuntimeException(e);
	} finally {
	    try {
		if (!hasMore) {
		    closable.close();
		}
	    } catch (IOException e) {
		LOG.error("Error closing on last message", e);
		throw (new RuntimeException(e));
	    }
	}
    }

    public void remove() {
	// Do nothing on purpose
    }

}
