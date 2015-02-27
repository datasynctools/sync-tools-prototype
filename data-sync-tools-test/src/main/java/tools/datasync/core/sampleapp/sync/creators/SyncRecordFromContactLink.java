package tools.datasync.core.sampleapp.sync.creators;

import tools.datasync.api.dao.HashFromObject;
import tools.datasync.api.utils.AbstractSyncRecordFromT;
import tools.datasync.core.sampleapp.model.ContactLink;

public class SyncRecordFromContactLink extends
	AbstractSyncRecordFromT<ContactLink> {

    public SyncRecordFromContactLink(HashFromObject hasher) {
	super(hasher);
    }

    public String getRecordId(ContactLink item) {
	return (item.getSourceContactId() + "-" + item.getTargetContactId()
		+ "-" + item.getWorkHistoryId());
    }
}
