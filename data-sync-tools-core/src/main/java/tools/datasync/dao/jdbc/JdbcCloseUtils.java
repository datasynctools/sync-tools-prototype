package tools.datasync.dao.jdbc;


public class JdbcCloseUtils {

    public static void closeRuntimeException(AutoCloseable... closables) {
	try {
	    for (AutoCloseable closable : closables) {
		if (closable != null) {
		    closable.close();
		}

	    }
	} catch (Exception e) {
	    throw (new RuntimeException(e));
	}
    }

}
