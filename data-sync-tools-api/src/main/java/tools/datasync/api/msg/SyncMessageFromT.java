package tools.datasync.api.msg;

public interface SyncMessageFromT<T> {
    SyncMessage create(T item);
}
