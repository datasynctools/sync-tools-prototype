package tools.datasync.api.dao;

public interface SyncRecordFromT<T> {
    SyncRecord create(T item);

    String getEntityId();
}
