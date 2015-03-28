package tools.datasync.dao;

import java.io.Closeable;
import java.sql.ResultSet;
import java.sql.SQLException;

import tools.datasync.api.msg.SyncEntityMessage;
import tools.datasync.basic.model.DefaultSyncEntityMessage;

public class SyncEntityMessageResultMapper implements
	ResultMapper<SyncEntityMessage> {

    public SyncEntityMessage map(ResultSet result, String entityName,
	    Closeable closable) throws SQLException {
	if (result.next()) {

	    SyncEntityMessage syncEntityMsg = new DefaultSyncEntityMessage();
	    syncEntityMsg.setEntity(entityName);
	    int count = result.getMetaData().getColumnCount();
	    for (int index = 1; index <= count; index++) {
		String columnName = result.getMetaData().getColumnName(index);
		Object value = result.getObject(index);

		syncEntityMsg.set(columnName.toUpperCase(), value);
	    }

	    return syncEntityMsg;
	} else {
	    return null;
	}

    }

}
