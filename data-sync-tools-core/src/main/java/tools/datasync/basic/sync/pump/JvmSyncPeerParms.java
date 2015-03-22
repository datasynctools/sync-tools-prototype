package tools.datasync.basic.sync.pump;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

import tools.datasync.basic.comm.SyncMessage;
import tools.datasync.basic.sync.SyncPeer;

public class JvmSyncPeerParms {

    private SyncPeer syncPeer;
    private BlockingQueue<SyncMessage> sendQueue;
    private BlockingQueue<SyncMessage> receiveQueue;

    private CountDownLatch ackPairReceiverLatch = new CountDownLatch(1);
    private CountDownLatch ackPeerSenderLatchA = new CountDownLatch(1);
    private CopyOnWriteArrayList<String> arrayList;

    public JvmSyncPeerParms(SyncPeer syncPeer,
	    BlockingQueue<SyncMessage> sendQueue,
	    BlockingQueue<SyncMessage> receiveQueue,
	    CopyOnWriteArrayList<String> arrayList) {
	this.syncPeer = syncPeer;
	this.sendQueue = sendQueue;
	this.receiveQueue = receiveQueue;
	this.arrayList = arrayList;
    }

    public SyncPeer getSyncPeer() {
	return syncPeer;
    }

    public BlockingQueue<SyncMessage> getSendQueue() {
	return sendQueue;
    }

    public BlockingQueue<SyncMessage> getReceiveQueue() {
	return receiveQueue;
    }

    public CopyOnWriteArrayList<String> getArrayList() {
	return arrayList;
    }

    public CountDownLatch getAckPairReceiverLatch() {
	return ackPairReceiverLatch;
    }

    public CountDownLatch getAckPeerSenderLatch() {
	return ackPeerSenderLatchA;
    }

}
