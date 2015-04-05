package tools.datasync.core.sampleapp.metamodel.test;

import static org.apache.metamodel.schema.ColumnType.VARCHAR;

import org.apache.metamodel.UpdateCallback;
import org.apache.metamodel.UpdateScript;
import org.apache.metamodel.UpdateableDataContext;
import org.apache.metamodel.create.TableCreationBuilder;

public class SyncStateTableCreator {

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
		updateableContext.getDefaultSchema(), "SyncState");

	// TODO Add index to EntityId
	tableBuilder.withColumn("EntityId").ofType(VARCHAR).ofSize(36);
	tableBuilder.withColumn("RecordId").ofType(VARCHAR).ofSize(36);
	tableBuilder.withColumn("RecordHash").ofType(VARCHAR).ofSize(100);
	tableBuilder.withColumn("RecordData").ofType(VARCHAR).ofSize(2000);
	tableBuilder.execute();
    }

}
