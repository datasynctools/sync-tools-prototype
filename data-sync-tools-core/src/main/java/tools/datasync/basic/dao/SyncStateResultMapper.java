package tools.datasync.basic.dao;

import java.io.Closeable;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import tools.datasync.basic.model.SyncEntityMessage;

public class SyncStateResultMapper implements ResultMapper<SyncEntityMessage> {

    private String syncStateTableName;

    public SyncStateResultMapper(String syncStateTableName) {
	this.syncStateTableName = syncStateTableName;
    }

    public SyncEntityMessage map(ResultSet result, String entityName,
	    Closeable closable) throws SQLException, IOException {
	if (result.next()) {

	    SyncEntityMessage json = new SyncEntityMessage();
	    json.setEntity(syncStateTableName);
	    int count = result.getMetaData().getColumnCount();
	    for (int index = 1; index <= count; index++) {
		String columnName = result.getMetaData().getColumnName(index);
		Object value = result.getObject(index);

		json.set(columnName.toUpperCase(), value);
	    }

	    return json;
	} else {
	    closable.close();
	    return null;
	}

    }

}
