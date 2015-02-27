package tools.datasync.core.sampleapp.metamodel.test;

import static org.apache.metamodel.schema.ColumnType.CLOB;
import static org.apache.metamodel.schema.ColumnType.DATE;
import static org.apache.metamodel.schema.ColumnType.SMALLINT;
import static org.apache.metamodel.schema.ColumnType.VARCHAR;

import org.apache.metamodel.UpdateCallback;
import org.apache.metamodel.UpdateScript;
import org.apache.metamodel.UpdateableDataContext;
import org.apache.metamodel.create.TableCreationBuilder;

public class ContactTableCreator {

    public static void createDb(final UpdateableDataContext updateableContext) {
	updateableContext.refreshSchemas();
	UpdateScript updateScript = new UpdateScript() {
	    public void run(UpdateCallback callback) {

		tableCreator(callback, updateableContext);

	    }
	};

	updateableContext.executeUpdate(updateScript);

    }

    private static void tableCreator(UpdateCallback callback,
	    UpdateableDataContext updateableContext) {

	// CREATE a table
	TableCreationBuilder tableBuilder = callback.createTable(
		updateableContext.getDefaultSchema(), "Contact");
	addContactId(tableBuilder);
	addDateOfBirth(tableBuilder);
	addFirstName(tableBuilder);
	addLastName(tableBuilder);
	addHeightFt(tableBuilder);
	addHeightInch(tableBuilder);
	addPicture(tableBuilder);
	addPreferredHeight(tableBuilder);

	tableBuilder.execute();
    }

    private static void addContactId(TableCreationBuilder tableBuilder) {
	tableBuilder.withColumn("ContactId").ofType(VARCHAR).ofSize(36)
		.asPrimaryKey();
    }

    private static void addDateOfBirth(TableCreationBuilder tableBuilder) {
	tableBuilder.withColumn("DateOfBirth").ofType(DATE);
    }

    private static void addFirstName(TableCreationBuilder tableBuilder) {
	tableBuilder.withColumn("FirstName").ofType(VARCHAR).ofSize(25);
    }

    private static void addLastName(TableCreationBuilder tableBuilder) {
	tableBuilder.withColumn("LastName").ofType(VARCHAR).ofSize(25);
    }

    private static void addHeightFt(TableCreationBuilder tableBuilder) {
	tableBuilder.withColumn("HeightFt").ofType(SMALLINT);
    }

    private static void addHeightInch(TableCreationBuilder tableBuilder) {
	tableBuilder.withColumn("HeightInch").ofType(SMALLINT);
    }

    private static void addPicture(TableCreationBuilder tableBuilder) {
	tableBuilder.withColumn("Picture").ofType(CLOB);
    }

    private static void addPreferredHeight(TableCreationBuilder tableBuilder) {
	tableBuilder.withColumn("PreferredHeight").ofType(VARCHAR).ofSize(25);

    }
}
