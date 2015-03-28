package tools.datasync.dao;

import java.io.Closeable;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultMapper<T> {
    T map(ResultSet result, String entityName, Closeable closable)
	    throws SQLException, IOException;
}
