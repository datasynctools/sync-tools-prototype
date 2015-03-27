package tools.datasync.core.sampleapp.sync.creators;

import tools.datasync.api.utils.AbstractSyncRecordFromT;
import tools.datasync.core.sampleapp.model.WorkHistory;
import tools.datasync.utils.HashFromObject;

public class SyncRecordFromWorkHIstory extends
	AbstractSyncRecordFromT<WorkHistory> {

    public SyncRecordFromWorkHIstory(HashFromObject hasher) {
	super(hasher);
    }

    public String getRecordId(WorkHistory item) {
	return (item.getWorkHistoryId());
    }
}
