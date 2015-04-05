package tools.datasync.dao;

import tools.datasync.api.dao.SyncRecord;
import tools.datasync.api.dao.SyncRecordFromT;
import tools.datasync.api.utils.HashFromObject;
import tools.datasync.api.utils.Stringify;
import tools.datasync.dataformats.json.Jsonify;

public abstract class AbstractSyncRecordFromT<T> implements SyncRecordFromT<T> {

    private String entityId = null;
    private HashFromObject hasher;
    private Stringify stringify = new Jsonify();

    public AbstractSyncRecordFromT(HashFromObject hasher, String entityId) {
	this.hasher = hasher;
	this.entityId = entityId;
    }

    public SyncRecord create(T item) {
	SyncRecord answer = new SyncRecord();
	// TODO Create a factory for getting the entity id and cache it
	answer.setEntityId(entityId);
	answer.setRecordId(getRecordId(item));
	answer.setRecordHash(hasher.createHash(item));
	// TODO hard coded to hack an example
	answer.setRecordData(stringify.toString(item));
	// TODO Set origin by using a Stringify instance
	// answer.setOrigin(origin);
	return answer;
    }

    public String getEntityId() {
	return (entityId);
    }

    public void setStringify(Stringify stringify) {
	this.stringify = stringify;
    }

    abstract public String getRecordId(T item);

}
