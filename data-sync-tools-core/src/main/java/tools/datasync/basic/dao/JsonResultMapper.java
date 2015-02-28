package tools.datasync.basic.dao;

import java.io.Closeable;
import java.sql.ResultSet;
import java.sql.SQLException;

import tools.datasync.basic.model.JSON;

public class JsonResultMapper implements ResultMapper<JSON> {

    public JSON map(ResultSet result, String entityName, Closeable closable)
	    throws SQLException {
	if (result.next()) {

	    JSON json = new JSON(entityName);
	    int count = result.getMetaData().getColumnCount();
	    for (int index = 1; index <= count; index++) {
		String columnName = result.getMetaData().getColumnName(index);
		Object value = result.getObject(index);

		json.set(columnName.toUpperCase(), value);
	    }

	    return json;
	} else {
	    return null;
	}

    }

}
