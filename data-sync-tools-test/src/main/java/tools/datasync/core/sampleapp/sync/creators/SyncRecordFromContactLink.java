package tools.datasync.core.sampleapp.sync.creators;

import tools.datasync.api.utils.AbstractSyncRecordFromT;
import tools.datasync.core.sampleapp.model.ContactLink;
import tools.datasync.utils.HashFromObject;

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
