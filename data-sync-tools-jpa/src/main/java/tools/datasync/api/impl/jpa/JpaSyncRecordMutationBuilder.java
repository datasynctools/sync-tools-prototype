package tools.datasync.api.impl.jpa;

import tools.datasync.api.dao.SyncRecord;
import tools.datasync.api.dao.SyncRecordMutationBuilder;

public class JpaSyncRecordMutationBuilder
	implements
	SyncRecordMutationBuilder<Object, JpaSyncRecord, JpaSyncRecord, JpaSyncRecord> {

    private JpaSyncRecord convertTo(SyncRecord syncRecord) {
	JpaSyncRecord jpaRecord = new JpaSyncRecord();
	jpaRecord.setEntityId(syncRecord.getEntityId());
	// jpaRecord.setOrigin(syncRecord.getOrigin());
	jpaRecord.setRecordData(syncRecord.getRecordData());
	jpaRecord.setRecordHash(syncRecord.getRecordHash());
	jpaRecord.setRecordId(syncRecord.getRecordId());
	return (jpaRecord);
    }

    public JpaSyncRecord createInsertSyncData(Object empty,
	    final SyncRecord syncRecord) {
	return (convertTo(syncRecord));
    }

    public JpaSyncRecord createUpdateSyncData(Object empty,
	    final SyncRecord syncRecord) {
	return (convertTo(syncRecord));

    }

    public JpaSyncRecord createDeleteSyncData(Object context,
	    SyncRecord syncRecord) {
	return (convertTo(syncRecord));

    }

}
