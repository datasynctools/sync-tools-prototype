package tools.datasync.api.impl.metamodel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.metamodel.UpdateCallback;
import org.apache.metamodel.UpdateScript;
import org.apache.metamodel.UpdateableDataContext;

import tools.datasync.api.dao.SyncEntityGroup;
import tools.datasync.api.dao.SyncEntityGroupType;
import tools.datasync.api.dao.SyncRecord;
import tools.datasync.api.dao.SyncStateDao;

public class MetadataSyncStateDao implements SyncStateDao {

    // private final static Logger LOG = LoggerFactory
    // .getLogger(MetadataSyncStateDao.class);

    private Map<SyncEntityGroupType, List<String>> orderedProcessing = null;

    private UpdateableDataContext dataContext;
    private static final MetadataSyncRecordMutationBuilder builder = new MetadataSyncRecordMutationBuilder();

    public MetadataSyncStateDao(UpdateableDataContext updateableContext) {
	this.dataContext = updateableContext;
    }

    public void addRecord(final SyncRecord record) {
	// TODO This code may need to become aware of all of the peers and write
	// to a separate Peer
	UpdateScript updateScript = new UpdateScript() {
	    public void run(UpdateCallback callback) {

		builder.createInsertSyncData(callback, record).execute();

	    }
	};

	dataContext.executeUpdate(updateScript);

    }

    public void updateRecord(final SyncRecord record) {

	UpdateScript updateScript = new UpdateScript() {
	    public void run(UpdateCallback callback) {

		builder.createUpdateSyncData(callback, record).execute();

	    }
	};

	dataContext.executeUpdate(updateScript);

    }

    public void deleteRecord(final String entityId, final String recordId) {

	UpdateScript updateScript = new UpdateScript() {
	    public void run(UpdateCallback callback) {
		callback.deleteFrom("SyncState").where("EntityId").eq(entityId)
			.where("RecordId").eq(recordId).execute();
	    }
	};

	dataContext.executeUpdate(updateScript);

    }

    Iterator<SyncRecord> getMutatedRecordsByEntityId(String entityId) {
	return null;
    }

    Iterator<SyncRecord> getDeletedRecordsByEntityId(String entityId) {
	return null;
    }

    private void processMutation(List<SyncEntityGroup> orderedChanges) {
	List<String> orderedMutatedEntities = orderedProcessing
		.get(SyncEntityGroupType.MUTATED);

	for (String entityId : orderedMutatedEntities) {
	    Iterator<SyncRecord> records = getMutatedRecordsByEntityId(entityId);
	    SyncEntityGroup group = new SyncEntityGroup(entityId, records,
		    SyncEntityGroupType.MUTATED);
	    orderedChanges.add(group);
	}
    }

    private void processDeletion(List<SyncEntityGroup> orderedChanges) {
	List<String> orderedDeletedEntities = orderedProcessing
		.get(SyncEntityGroupType.MUTATED);

	for (String entityId : orderedDeletedEntities) {
	    Iterator<SyncRecord> records = getDeletedRecordsByEntityId(entityId);
	    SyncEntityGroup group = new SyncEntityGroup(entityId, records,
		    SyncEntityGroupType.DELETED);
	    orderedChanges.add(group);
	}
    }

    // TODO Double check that this is an ordered list (I think the interface
    // needs to change to reflect the ordered need)
    public List<SyncEntityGroup> getOrderedChangesByPeerId(String peerId) {

	// TODO Double check that this is an ordered list!
	List<SyncEntityGroup> orderedChanges = new ArrayList<SyncEntityGroup>();

	processMutation(orderedChanges);

	processDeletion(orderedChanges);

	return (orderedChanges);

	// DataSet dataSet = doQuery(dataContext);
	//
	// List<Contact> contacts = new ArrayList<Contact>();
	//
	// if (dataSet != null) {
	// Iterator<Row> iterator = dataSet.iterator();
	// while (iterator.hasNext()) {
	// Row row = iterator.next();
	// addContactToList(row, contacts);
	// }
	// } else {
	// LOG.debug("Dataset is null, no records found");
	// }
	// return (contacts);
    }

    // private final static DataSet doQuery(UpdateableDataContext dataContext,
    // String entityId, SyncEntityGroupType type) {
    // DataSet dataSet = dataContext.query().from("SyncState")
    // .select("EntityId").select("RecordId").select("RecordHash")
    // .select("RecordData").where("EntityId").eq(entityId).execute();
    // return (dataSet);
    //
    // }

    public void markPeerRecordProcessing(String peerId, String entityId,
	    String recordId) {
	// TODO Auto-generated method stub

    }

    public void markPeerRecordProcessed(String peerId, String entityId,
	    String recordId) {
	// TODO Auto-generated method stub

    }

}
