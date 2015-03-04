package tools.datasync.core.sampleapp;

import tools.datasync.basic.model.EntityGetter;
import tools.datasync.basic.model.IdGetter;

public class SampleSyncIdGetter implements IdGetter {

    private EntityGetter entityGetter;

    public SampleSyncIdGetter(EntityGetter entityGetter) {
	this.entityGetter = entityGetter;
    }

    public String get(String entityName) {
	return (entityGetter.getId(entityName));
    }

}
