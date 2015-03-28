package tools.datasync.core.sampleapp;

import tools.datasync.api.dao.EntityGetter;
import tools.datasync.api.dao.IdGetter;

public class SampleSyncIdGetter implements IdGetter {

    private EntityGetter entityGetter;

    public SampleSyncIdGetter(EntityGetter entityGetter) {
	this.entityGetter = entityGetter;
    }

    public String get(String entityName) {
	return (entityGetter.getId(entityName));
    }

}
