package tools.datasync.core.sampleapp.sync.creators;

import tools.datasync.api.utils.HashFromObject;
import tools.datasync.core.sampleapp.model.WorkHistory;
import tools.datasync.dao.AbstractSyncRecordFromT;

public class SyncRecordFromWorkHIstory extends
	AbstractSyncRecordFromT<WorkHistory> {

    public SyncRecordFromWorkHIstory(HashFromObject hasher, String entityId) {
	super(hasher, entityId);
    }

    public String getRecordId(WorkHistory item) {
	return (item.getWorkHistoryId());
    }
}
