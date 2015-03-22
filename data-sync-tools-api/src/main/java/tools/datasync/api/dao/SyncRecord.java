package tools.datasync.api.dao;

public class SyncRecord {

    private String entityId;
    private String recordId;
    private String recordHash;
    private String recordData;
    private String origin;

    public String getEntityId() {
	return entityId;
    }

    public void setEntityId(String entityId) {
	this.entityId = entityId;
    }

    public String getRecordId() {
	return recordId;
    }

    public void setRecordId(String recordId) {
	this.recordId = recordId;
    }

    public String getRecordHash() {
	return recordHash;
    }

    public void setRecordHash(String recordHash) {
	this.recordHash = recordHash;
    }

    public String getRecordData() {
	return recordData;
    }

    public void setRecordData(String recordData) {
	this.recordData = recordData;
    }

    public String getOrigin() {
	return origin;
    }

    public void setOrigin(String origin) {
	this.origin = origin;
    }

}
