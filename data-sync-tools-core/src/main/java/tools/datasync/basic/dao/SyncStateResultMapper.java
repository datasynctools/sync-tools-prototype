package tools.datasync.basic.dao;

import java.io.Closeable;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import tools.datasync.basic.model.JSON;

public class SyncStateResultMapper implements ResultMapper<JSON> {

    private String syncStateTableName;

    public SyncStateResultMapper(String syncStateTableName) {
	this.syncStateTableName = syncStateTableName;
    }

    public JSON map(ResultSet result, String entityName, Closeable closable)
	    throws SQLException, IOException {
	if (result.next()) {

	    JSON json = new JSON(syncStateTableName);
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
