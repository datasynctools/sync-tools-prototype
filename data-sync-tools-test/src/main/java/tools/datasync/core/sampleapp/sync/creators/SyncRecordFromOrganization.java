package tools.datasync.core.sampleapp.sync.creators;

import tools.datasync.api.utils.AbstractSyncRecordFromT;
import tools.datasync.core.sampleapp.model.Organization;
import tools.datasync.utils.HashFromObject;

public class SyncRecordFromOrganization extends
	AbstractSyncRecordFromT<Organization> {

    public SyncRecordFromOrganization(HashFromObject hasher) {
	super(hasher);
    }

    public String getRecordId(Organization item) {
	return (item.getOrganizationId());
    }
}
