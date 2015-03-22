package tools.datasync.api.impl.jdbc;

import tools.datasync.api.dao.SyncRecord;

public class SyncRecordInsertHelper {

    private static final String TABLE_COLUMNS = "(EntityId, RecordId, "
	    + "RecordHash, RecordData)";

    public static final String prepareInsert(SyncRecord item,
	    String syncStateTableName) {
	StringBuilder builder = new StringBuilder();
	builder.append("INSERT INTO ");

	builder.append(syncStateTableName);
	builder.append(" ");

	builder.append(TABLE_COLUMNS);

	builder.append(" VALUES (");

	addEntityIdSql(item, builder);
	addRecordIdSql(item, builder);
	addRecordHashSql(item, builder);
	addRecordDataSql(item, builder);

	// Note: stripping last comma, so re-ordering is easier in the future
	// (if needed)
	String answer = builder.toString().toString()
		.substring(0, builder.toString().length() - 1);

	// Add final right parenthesis on returning
	return (answer + ")");
    }

    private static final void addEntityIdSql(SyncRecord item,
	    StringBuilder builder) {
	builder.append(" '");
	builder.append(item.getEntityId());
	builder.append("',");
    }

    private static final void addRecordIdSql(SyncRecord item,
	    StringBuilder builder) {
	builder.append(" '");
	builder.append(item.getRecordId());
	builder.append("',");
    }

    private static final void addRecordHashSql(SyncRecord item,
	    StringBuilder builder) {
	builder.append(" '");
	builder.append(item.getRecordHash());
	builder.append("',");
    }

    private static final void addRecordDataSql(SyncRecord item,
	    StringBuilder builder) {
	builder.append(" '");
	builder.append(item.getRecordData());
	builder.append("',");
    }

}
