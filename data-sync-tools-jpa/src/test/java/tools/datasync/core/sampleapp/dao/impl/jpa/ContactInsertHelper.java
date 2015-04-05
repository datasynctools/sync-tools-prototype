package tools.datasync.core.sampleapp.dao.impl.jpa;

import tools.datasync.core.sampleapp.model.Contact;

public class ContactInsertHelper {

    private static final String TABLE_COLUMNS = "(ContactId, DateOfBirth, "
	    + "FirstName, LastName, HeightFt, HeightInch, Picture, PreferredHeight)";

    public static final String prepareInsert(Contact item,
	    String contactTableName) {

	StringBuilder builder = new StringBuilder();
	builder.append("INSERT INTO ");

	builder.append(contactTableName);
	builder.append(" ");
	builder.append(TABLE_COLUMNS);

	builder.append(" Values (");

	addContactIdSql(item, builder);
	addDateOfBirthSql(item, builder);
	addFirstNameSql(item, builder);
	addLastNameSql(item, builder);
	addHeightFtSql(item, builder);
	addHeightInchSql(item, builder);
	// addPictureSql(item, builder);
	addPreferredHeightSql(item, builder);

	// Note: stripping last comma, so re-ordering is easier in the future
	// (if needed)
	String answer = builder.toString().toString()
		.substring(0, builder.toString().length() - 1);

	// Add final right parenthesis on returning
	return (answer + ")");

    }

    // private static final void addPictureSql(Contact item, StringBuilder
    // builder) {
    // builder.append(item.getPicture());
    // builder.append(",");
    // }

    private static final void addString(StringBuilder builder, String value) {
	if (value == null) {
	    builder.append(" null,");
	    // builder.append(value);
	    // builder.append(",");
	    return;
	}
	builder.append(" '");
	builder.append(value);
	builder.append("',");

    }

    private static final void addPreferredHeightSql(Contact item,
	    StringBuilder builder) {
	addString(builder, item.getPreferredHeight());
    }

    private static final void addContactIdSql(Contact item,
	    StringBuilder builder) {
	addString(builder, item.getContactId());
    }

    private static final void addDateOfBirthSql(Contact item,
	    StringBuilder builder) {
	builder.append(item.getDateOfBirth());
	builder.append(",");
    }

    private static final void addFirstNameSql(Contact item,
	    StringBuilder builder) {
	addString(builder, item.getFirstName());
    }

    private static final void addLastNameSql(Contact item, StringBuilder builder) {
	addString(builder, item.getLastName());
    }

    private static final void addHeightFtSql(Contact item, StringBuilder builder) {
	builder.append(item.getHeightFt());
	builder.append(",");
    }

    private static final void addHeightInchSql(Contact item,
	    StringBuilder builder) {
	builder.append(item.getHeightInch());
	builder.append(",");
    }
}
