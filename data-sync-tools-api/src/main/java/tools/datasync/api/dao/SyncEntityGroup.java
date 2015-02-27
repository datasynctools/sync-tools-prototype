package tools.datasync.api.dao;

import java.util.Iterator;

public class SyncEntityGroup {

    private String entityId;
    private Iterator<SyncRecord> records;
    private SyncEntityGroupType syncEntityGroupType;

    public SyncEntityGroup(String entityId, Iterator<SyncRecord> records,
	    SyncEntityGroupType syncEntityGroupType) {
	this.records = records;
	this.syncEntityGroupType = syncEntityGroupType;
    }

    public String getEntityId() {
	return entityId;
    }

    public Iterator<SyncRecord> getRecords() {
	return records;
    }

    public SyncEntityGroupType getSyncEntityGroupType() {
	return syncEntityGroupType;
    }

}
