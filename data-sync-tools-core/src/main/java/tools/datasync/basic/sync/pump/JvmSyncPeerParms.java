package tools.datasync.basic.sync.pump;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

import tools.datasync.basic.sync.SyncPeer;

public class JvmSyncPeerParms {
    private SyncPeer syncPeer;
    private BlockingQueue<String> queue;
    // private JvmSyncPeerDbParms dbParms;

    private CountDownLatch ackPairReceiverLatch = new CountDownLatch(1);
    private CountDownLatch ackPeerSenderLatchA = new CountDownLatch(1);

    public JvmSyncPeerParms(SyncPeer syncPeer, BlockingQueue<String> queue) {
	this.syncPeer = syncPeer;
	this.queue = queue;
	// this.dbParms = dbParms;
    }

    public SyncPeer getSyncPeer() {
	return syncPeer;
    }

    public BlockingQueue<String> getQueue() {
	return queue;
    }

    // public JvmSyncPeerDbParms getDbParms() {
    // return dbParms;
    // }

    public CountDownLatch getAckPairReceiverLatch() {
	return ackPairReceiverLatch;
    }

    public CountDownLatch getAckPeerSenderLatch() {
	return ackPeerSenderLatchA;
    }

}