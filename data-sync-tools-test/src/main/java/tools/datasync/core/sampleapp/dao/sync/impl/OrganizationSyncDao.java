package tools.datasync.core.sampleapp.dao.sync.impl;

import java.util.List;

import tools.datasync.api.dao.SyncRecord;
import tools.datasync.api.dao.SyncRecordFromT;
import tools.datasync.api.dao.SyncStateDao;
import tools.datasync.core.sampleapp.dao.OrganizationDao;
import tools.datasync.core.sampleapp.model.Organization;

public class OrganizationSyncDao implements OrganizationDao {

    private OrganizationDao dao;
    private SyncStateDao syncStateDao;
    private SyncRecordFromT<Organization> syncRecordCreator;
    private String entityId;

    public OrganizationSyncDao(OrganizationDao dao, SyncStateDao syncStateDao,
	    SyncRecordFromT<Organization> syncRecordCreator) {
	this.dao = dao;
	this.syncStateDao = syncStateDao;
	this.syncRecordCreator = syncRecordCreator;
	entityId = syncRecordCreator.getEntityId();
    }

    public void addOrganization(Organization item) {
	SyncRecord syncRecord = syncRecordCreator.create(item);
	syncStateDao.addRecord(syncRecord);
	dao.addOrganization(item);
    }

    public void removeOrganization(String id) {
	syncStateDao.deleteRecord(entityId, id);
	dao.removeOrganization(id);
    }

    public List<Organization> getOrganizations() {
	return dao.getOrganizations();
    }

    public List<Organization> getOrganizationByContactId(String contactId) {
	return dao.getOrganizationByContactId(contactId);
    }

    public void updateOrganization(Organization item) {
	SyncRecord syncRecord = syncRecordCreator.create(item);
	syncStateDao.updateRecord(syncRecord);
	dao.updateOrganization(item);
    }

}
