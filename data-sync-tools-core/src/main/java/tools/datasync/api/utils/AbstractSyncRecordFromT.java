package tools.datasync.api.utils;

import tools.datasync.api.dao.SyncRecord;
import tools.datasync.api.dao.SyncRecordFromT;
import tools.datasync.utils.HashFromObject;

public abstract class AbstractSyncRecordFromT<T> implements SyncRecordFromT<T> {

    private String entityId = null;
    private HashFromObject hasher;

    public AbstractSyncRecordFromT(HashFromObject hasher) {
	this.hasher = hasher;
    }

    public SyncRecord create(T item) {
	SyncRecord answer = new SyncRecord();
	// TODO Create a factory for getting the entity id and cache it
	answer.setEntityId(entityId);
	answer.setRecordId(getRecordId(item));
	answer.setRecordHash(hasher.createHash(item));
	// TODO Set origin by using a Stringify instance
	// answer.setOrigin(origin);
	return answer;
    }

    public String getEntityId() {
	return (entityId);
    }

    abstract public String getRecordId(T item);

}
