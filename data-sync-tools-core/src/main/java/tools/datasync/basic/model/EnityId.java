package tools.datasync.basic.model;

import tools.datasync.basic.comm.SyncPayloadData;

public class EnityId extends SyncPayloadData {

    private String entityId;

    public String getEntityId() {
	return entityId;
    }

    public void setEntityId(String entityId) {
	this.entityId = entityId;
    }

}
