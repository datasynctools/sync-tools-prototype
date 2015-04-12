package tools.datasync.api.impl.jpa;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SyncState")
public class JpaSyncRecord implements Serializable {

    private static final long serialVersionUID = -621226742190550817L;

    @Id
    @Column(name = "EntityId")
    private String entityId;
    @Id
    private String recordId;
    @Column(length = 128)
    private String recordHash;
    @Column
    private String recordData;

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

}
