package tools.datasync.core.sampleapp;

import tools.datasync.basic.model.IdGetter;

public class SampleAppTableNameGetter implements IdGetter {

    public static interface Table {

	public static final String CONTACT = "org.Contact";
	public static final String CONTACT_LINK = "org.ContactLink";
	public static final String WORK_HISTORY = "org.WorkHistory";

	public static final String SYNC_ENTITY = "seed.SyncEntity";
	public static final String SYNC_PEER = "seed.SyncPeer";
	public static final String SYNC_STATE = "seed.SyncState";
    }

    public static class KeyColumn {

	public static final String PK_CONTACT = "CONTACTID";
	public static final String PK_CONTACT_LINK = "SOURCECONTACTID, TARGETCONTACTID, WORKHISTORYID";
	public static final String PK_WORK_HISTORY = "WORKHISTORYID";

	public static final String PK_SYNC_ENTITY = "ENTITYID";
	public static final String PK_SYNC_PEER = "PEERID";
	public static final String PK_SYNC_STATE = "ENTITYID, RECORDID";
    }

    public String get(String entityName) {

	if (Table.CONTACT.equalsIgnoreCase(entityName)) {
	    return KeyColumn.PK_CONTACT;

	} else if (Table.CONTACT_LINK.equalsIgnoreCase(entityName)) {
	    return KeyColumn.PK_CONTACT_LINK;

	} else if (Table.WORK_HISTORY.equalsIgnoreCase(entityName)) {
	    return KeyColumn.PK_WORK_HISTORY;

	} else if (Table.SYNC_ENTITY.equalsIgnoreCase(entityName)) {
	    return KeyColumn.PK_SYNC_ENTITY;

	} else if (Table.SYNC_PEER.equalsIgnoreCase(entityName)) {
	    return KeyColumn.PK_SYNC_PEER;

	} else if (Table.SYNC_STATE.equalsIgnoreCase(entityName)) {
	    return KeyColumn.PK_SYNC_STATE;

	}
	throw (new RuntimeException("No entity name of [" + entityName + "]"));
    }

}
