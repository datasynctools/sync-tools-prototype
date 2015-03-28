package tools.datasync.core.sampleapp;

import java.util.HashMap;
import java.util.Map;

import tools.datasync.basic.model.IdGetter;
import tools.datasync.utils.StringUtils;

public class SampleAppTableNameGetter implements IdGetter {

    Map<String, String> entityNames = new HashMap<String, String>();

    public SampleAppTableNameGetter() {
	entityNames.put(Table.CONTACT, KeyColumn.PK_CONTACT);
	entityNames.put(Table.CONTACT_LINK, KeyColumn.PK_CONTACT_LINK);
	entityNames.put(Table.WORK_HISTORY, KeyColumn.PK_WORK_HISTORY);

	entityNames.put(Table.SYNC_ENTITY, KeyColumn.PK_SYNC_ENTITY);
	entityNames.put(Table.SYNC_PEER, KeyColumn.PK_SYNC_PEER);
	entityNames.put(Table.SYNC_STATE, KeyColumn.PK_SYNC_STATE);
    }

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

	if (!entityNames.containsKey(entityName)) {
	    throw (new RuntimeException("No entity name of [" + entityName
		    + "]"));
	}
	String value = entityNames.get(entityName);
	return (value);
    }

    public String toString() {
	StringBuilder answer = new StringBuilder();
	answer.append(StringUtils.getSimpleName(this));
	answer.append("{");
	answer.append("entityNames=");
	answer.append(entityNames);
	answer.append("}");
	return (answer.toString());
    }

}
