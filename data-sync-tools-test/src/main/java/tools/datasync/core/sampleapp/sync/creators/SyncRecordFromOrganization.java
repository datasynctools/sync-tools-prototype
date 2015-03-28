package tools.datasync.core.sampleapp.sync.creators;

import tools.datasync.api.utils.HashFromObject;
import tools.datasync.core.sampleapp.model.Organization;
import tools.datasync.dao.AbstractSyncRecordFromT;

public class SyncRecordFromOrganization extends
	AbstractSyncRecordFromT<Organization> {

    public SyncRecordFromOrganization(HashFromObject hasher) {
	super(hasher);
    }

    public String getRecordId(Organization item) {
	return (item.getOrganizationId());
    }
}
