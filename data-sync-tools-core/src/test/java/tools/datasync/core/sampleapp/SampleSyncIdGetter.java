package tools.datasync.core.sampleapp;

import tools.datasync.basic.model.IdGetter;
import tools.datasync.basic.model.Ids.Table;

public class SampleSyncIdGetter implements IdGetter {

    public static class EntityId {

	public static final String UUID_CONTACT = "A157CC02-CC87-4296-947B-613CAABBDB40";
	public static final String UUID_CONTACT_LINK = "38C11B8D-E4F8-410A-B009-0EF8654A4681";
	public static final String UUID_WORK_HISTORY = "48E2F8D3-2A90-4A26-A198-ED649A4EFBA1";

	public static String getTableName(String entityId) {
	    if (UUID_CONTACT.equalsIgnoreCase(entityId)) {
		return Table.CONTACT;

	    } else if (UUID_CONTACT_LINK.equalsIgnoreCase(entityId)) {
		return Table.CONTACT_LINK;

	    } else if (UUID_WORK_HISTORY.equalsIgnoreCase(entityId)) {
		return Table.WORK_HISTORY;

	    }
	    return null;
	}
    }

    public String get(String entityName) {
	if (Table.CONTACT.equalsIgnoreCase(entityName)) {
	    return EntityId.UUID_CONTACT;

	} else if (Table.CONTACT_LINK.equalsIgnoreCase(entityName)) {
	    return EntityId.UUID_CONTACT_LINK;

	} else if (Table.WORK_HISTORY.equalsIgnoreCase(entityName)) {
	    return EntityId.UUID_WORK_HISTORY;

	}
	throw (new RuntimeException("No entity name of [" + entityName + "]"));
    }

}
