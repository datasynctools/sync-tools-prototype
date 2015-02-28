package tools.datasync.basic.dao;

import java.io.Closeable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import tools.datasync.basic.model.IdGetter;
import tools.datasync.basic.model.JSON;

public class JsonIteratorResultMapper implements ResultMapper<Iterator<JSON>> {

    private IdGetter idGetter;

    public JsonIteratorResultMapper(IdGetter idGetter) {
	this.idGetter = idGetter;
    }

    public Iterator<JSON> map(final ResultSet result, final String entityName,
	    final Closeable closable) throws SQLException {

	// String primaryKey = Ids.KeyColumn.get(entityName);
	String primaryKey = idGetter.get(entityName);
	String[] keys = primaryKey.split(",");
	final List<String> primaryKeyColumns = new ArrayList<String>();
	for (String key : keys) {
	    primaryKeyColumns.add(key.trim());
	}
	Collections.sort(primaryKeyColumns);

	return (new JdbcJsonIterator(result, entityName, primaryKeyColumns,
		closable));

    }

}