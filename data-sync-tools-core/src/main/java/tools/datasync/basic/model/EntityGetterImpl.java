package tools.datasync.basic.model;

import java.util.Map;

public class EntityGetterImpl implements EntityGetter {

    private String syncStateName;
    private Map<String, String> entityNameIdMap;

    public EntityGetterImpl(String syncStateName,
	    Map<String, String> entityNameIdMap) {
	this.syncStateName = syncStateName;
	this.entityNameIdMap = entityNameIdMap;
    }

    public String getId(String entityName) {
	if (!entityNameIdMap.containsKey(entityName)) {
	    throw (new RuntimeException("Could not find entity [" + entityName
		    + "]"));
	}
	String id = entityNameIdMap.get(entityName);
	return id;
    }

    public String getSyncStateName() {
	return syncStateName;
    }

    public String getName(String id) {
	for (Map.Entry<String, String> entry : entityNameIdMap.entrySet()) {
	    if (entry.getValue().equals(id)) {
		return entry.getKey();
	    }
	}
	throw (new RuntimeException("Could not find the name by id [" + id
		+ "]"));
    }

}