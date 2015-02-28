package tools.datasync.basic.dao;

import java.io.Closeable;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import tools.datasync.basic.model.Ids;
import tools.datasync.basic.model.JSON;

public class SyncStateResultMapper implements ResultMapper<JSON> {

    public JSON map(ResultSet result, String entityName, Closeable closable)
	    throws SQLException, IOException {
	if (result.next()) {

	    // TODO remove hard coding of sync state table
	    JSON json = new JSON(Ids.Table.SYNC_STATE);
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
