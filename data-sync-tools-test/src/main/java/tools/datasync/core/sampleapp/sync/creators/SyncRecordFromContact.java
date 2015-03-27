package tools.datasync.core.sampleapp.sync.creators;

import tools.datasync.api.utils.AbstractSyncRecordFromT;
import tools.datasync.core.sampleapp.model.Contact;
import tools.datasync.utils.HashFromObject;

public class SyncRecordFromContact extends AbstractSyncRecordFromT<Contact> {

    public SyncRecordFromContact(HashFromObject hasher) {
	super(hasher);
    }

    public String getRecordId(Contact item) {
	return (item.getContactId());
    }
}
