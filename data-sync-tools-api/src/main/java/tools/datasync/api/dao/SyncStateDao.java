package tools.datasync.api.dao;

import java.util.List;

public interface SyncStateDao {

    void addRecord(SyncRecord record);

    void updateRecord(SyncRecord record);

    void deleteRecord(String entityId, String recordId);

    void markPeerRecordProcessing(String peerId, String entityId,
	    String recordId);

    void markPeerRecordProcessed(String peerId, String entityId, String recordId);

    List<SyncEntityGroup> getOrderedChangesByPeerId(String peerId);

}
