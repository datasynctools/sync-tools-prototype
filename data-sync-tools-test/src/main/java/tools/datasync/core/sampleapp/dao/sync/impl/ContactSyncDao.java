package tools.datasync.core.sampleapp.dao.sync.impl;

import java.util.List;

import tools.datasync.api.dao.SyncRecord;
import tools.datasync.api.dao.SyncRecordFromT;
import tools.datasync.api.dao.SyncStateDao;
import tools.datasync.core.sampleapp.dao.ContactDao;
import tools.datasync.core.sampleapp.model.Contact;

public class ContactSyncDao implements ContactDao {

    private ContactDao dao;
    private SyncStateDao syncStateDao;
    private SyncRecordFromT<Contact> syncRecordCreator;
    private String entityId;

    public ContactSyncDao(ContactDao dao, SyncStateDao syncStateDao,
	    SyncRecordFromT<Contact> syncRecordCreator) {
	this.dao = dao;
	this.syncStateDao = syncStateDao;
	this.syncRecordCreator = syncRecordCreator;
	entityId = syncRecordCreator.getEntityId();
    }

    public void addContact(Contact item) {
	SyncRecord syncRecord = syncRecordCreator.create(item);
	syncStateDao.addRecord(syncRecord);
	dao.addContact(item);
    }

    public void removeContact(String id) {
	syncStateDao.deleteRecord(entityId, id);
	dao.removeContact(id);
    }

    public List<Contact> getContacts() {
	return dao.getContacts();
    }

    public void updateContact(Contact item) {
	SyncRecord syncRecord = syncRecordCreator.create(item);
	syncStateDao.updateRecord(syncRecord);
	dao.updateContact(item);
    }

}
