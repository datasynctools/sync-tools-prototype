package tools.datasync.dao.jdbc;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class JdbcSelectionClosable implements Closeable {

    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;

    public JdbcSelectionClosable(Connection connection, Statement statement,
	    ResultSet resultSet) {
	this.connection = connection;
	this.statement = statement;
	this.resultSet = resultSet;
    }

    public void close() throws IOException {
	JdbcCloseUtils.closeRuntimeException(connection, statement, resultSet);
    }

}
