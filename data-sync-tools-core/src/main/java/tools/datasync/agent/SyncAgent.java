package tools.datasync.agent;

import java.util.Iterator;
import java.util.List;

import tools.datasync.api.dao.SyncEntityGroup;
import tools.datasync.api.dao.SyncRecord;
import tools.datasync.api.dao.SyncStateDao;

//TODO Implement the to-be re-factored pumping and receiving code. This is just here as a design scratchpad
public class SyncAgent {

    private SyncStateDao syncStateDao = null;

    public void syncSender(String peerId) {
	List<SyncEntityGroup> changes = syncStateDao
		.getOrderedChangesByPeerId(peerId);

	for (SyncEntityGroup group : changes) {
	    Iterator<SyncRecord> records = group.getRecords();
	    while (records.hasNext()) {
		SyncRecord record = records.next();
		syncStateDao.markPeerRecordProcessing(peerId,
			record.getEntityId(), record.getRecordId());
		// TODO Send the message via the pumping infrastructure
		// (to-be re-factored)
	    }

	    // TODO Wait/block for acknowledgement that the group has been
	    // processed, then send the next group
	}

    }

    public void syncAckChanges(String peerId, List<SyncRecord> records) {

	for (SyncRecord record : records) {
	    syncStateDao.markPeerRecordProcessed(peerId, record.getEntityId(),
		    record.getRecordId());
	}

    }

    public void receiveChanges(String peerId, List<SyncRecord> records) {

	// for (SyncRecord record : records) {
	// // syncStateDao.
	// }

    }

}
