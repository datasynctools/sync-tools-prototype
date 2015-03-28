package tools.datasync.core.sampleapp.sync.creators;

import tools.datasync.api.utils.HashFromObject;
import tools.datasync.core.sampleapp.model.Contact;
import tools.datasync.dao.AbstractSyncRecordFromT;

public class SyncRecordFromContact extends AbstractSyncRecordFromT<Contact> {

    public SyncRecordFromContact(HashFromObject hasher) {
	super(hasher);
    }

    public String getRecordId(Contact item) {
	return (item.getContactId());
    }
}
