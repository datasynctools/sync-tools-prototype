package tools.datasync.api.dao;

public interface SyncRecordMutationBuilder<A, B, C, D> {

    B createInsertSyncData(A context, SyncRecord syncRecord);

    C createUpdateSyncData(A context, SyncRecord syncRecord);

    D createDeleteSyncData(A context, SyncRecord syncRecord);

}
