package tools.datasync.api.impl.jdbc;

import tools.datasync.api.dao.SyncRecord;

public class SyncRecordUpdateHelper {

    public static final String prepareUpdate(SyncRecord item) {

	StringBuilder builder = new StringBuilder();
	builder.append("UPDATE ");
	builder.append("SyncState ");
	builder.append("SET ");

	addEntityIdSql(item, builder);
	addRecordIdSql(item, builder);
	addRecordHashSql(item, builder);
	addRecordDataSql(item, builder);

	// Note: stripping last comma, so re-ordering is easier in the future
	// (if needed)
	String answer = builder.toString().toString()
		.substring(0, builder.toString().length() - 1);

	// Add final right parenthesis on returning
	answer = answer + " WHERE";
	answer = answer + " (EntityId='" + item.getEntityId() + "')";
	answer = answer + " AND (RecordId='" + item.getRecordId() + "')";

	return (answer);

    }

    private static final void addEntityIdSql(SyncRecord item,
	    StringBuilder builder) {
	builder.append("EntityId='");
	builder.append(item.getEntityId());
	builder.append("',");
    }

    private static final void addRecordIdSql(SyncRecord item,
	    StringBuilder builder) {
	builder.append("RecordId=");
	builder.append(item.getRecordId());
	builder.append(",");
    }

    private static final void addRecordHashSql(SyncRecord item,
	    StringBuilder builder) {
	builder.append("RecordHash='");
	builder.append(item.getRecordHash());
	builder.append("',");
    }

    private static final void addRecordDataSql(SyncRecord item,
	    StringBuilder builder) {
	builder.append("RecordData='");
	builder.append(item.getRecordJson());
	builder.append("',");
    }

}
