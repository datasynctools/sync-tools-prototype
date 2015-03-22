package tools.datasync.api.impl.metamodel;

import org.apache.metamodel.UpdateCallback;
import org.apache.metamodel.delete.RowDeletionBuilder;
import org.apache.metamodel.insert.RowInsertionBuilder;
import org.apache.metamodel.update.RowUpdationBuilder;

import tools.datasync.api.dao.SyncRecord;
import tools.datasync.api.dao.SyncRecordMutationBuilder;

public class MetadataSyncRecordMutationBuilder
	implements
	SyncRecordMutationBuilder<UpdateCallback, RowInsertionBuilder, RowUpdationBuilder, RowDeletionBuilder> {

    public RowInsertionBuilder createInsertSyncData(UpdateCallback context,
	    final SyncRecord syncRecord) {

	RowInsertionBuilder answer = context.insertInto("SyncState")
		.value("EntityId", syncRecord.getEntityId())
		.value("RecordId", syncRecord.getRecordId())
		.value("RecordHash", syncRecord.getRecordHash())
		.value("RecordData", syncRecord.getRecordData());

	return answer;
    }

    public RowUpdationBuilder createUpdateSyncData(UpdateCallback context,
	    final SyncRecord syncRecord) {

	RowUpdationBuilder answer = context.update("SyncState")
		.value("EntityId", syncRecord.getEntityId())
		.value("RecordId", syncRecord.getRecordId())
		.value("RecordHash", syncRecord.getRecordHash())
		.value("RecordData", syncRecord.getRecordData());

	return answer;
    }

    public RowDeletionBuilder createDeleteSyncData(UpdateCallback context,
	    SyncRecord syncRecord) {
	// TODO Complete delete design and implement
	return null;
    }

}
