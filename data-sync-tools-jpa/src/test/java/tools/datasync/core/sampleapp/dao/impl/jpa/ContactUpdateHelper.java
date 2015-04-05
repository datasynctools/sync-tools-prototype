package tools.datasync.core.sampleapp.dao.impl.jpa;

import tools.datasync.core.sampleapp.model.Contact;

public class ContactUpdateHelper {

    public static final String prepareUpdate(Contact item) {

	StringBuilder builder = new StringBuilder();
	builder.append("UPDATE ");
	builder.append("Contact ");
	builder.append("SET ");

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
	return (answer + " WHERE CustomerId='" + item.getContactId() + "'");

    }

    // private static final void addPictureSql(Contact item, StringBuilder
    // builder) {
    // builder.append("Picture=");
    // builder.append(item.getPicture());
    // builder.append(",");
    // }

    private static final void addPreferredHeightSql(Contact item,
	    StringBuilder builder) {
	builder.append("PreferredHeight='");
	builder.append(item.getPreferredHeight());
	builder.append("',");
    }

    private static final void addContactIdSql(Contact item,
	    StringBuilder builder) {
	builder.append("ContactId='");
	builder.append(item.getContactId());
	builder.append("',");
    }

    private static final void addDateOfBirthSql(Contact item,
	    StringBuilder builder) {
	builder.append("DateOfBirth=");
	builder.append(item.getDateOfBirth());
	builder.append(",");
    }

    private static final void addFirstNameSql(Contact item,
	    StringBuilder builder) {
	builder.append("FirstName='");
	builder.append(item.getFirstName());
	builder.append("',");
    }

    private static final void addLastNameSql(Contact item, StringBuilder builder) {
	builder.append("LastName='");
	builder.append(item.getLastName());
	builder.append("',");
    }

    private static final void addHeightFtSql(Contact item, StringBuilder builder) {
	builder.append("HeightFt=");
	builder.append(item.getHeightFt());
	builder.append(",");
    }

    private static final void addHeightInchSql(Contact item,
	    StringBuilder builder) {
	builder.append("HeightInch=");
	builder.append(item.getHeightInch());
	builder.append(",");
    }
}
