package tools.datasync.core.sampleapp.dao.impl.jpa;


public class ContactSelectHelper {

    public static final String prepareSelectAll(String contactTableName) {

	StringBuilder builder = new StringBuilder();
	builder.append("SELECT ");
	builder.append("* ");
	builder.append("FROM ");

	builder.append(contactTableName);

	// Add final right parenthesis on returning
	return (builder.toString());

    }

}
