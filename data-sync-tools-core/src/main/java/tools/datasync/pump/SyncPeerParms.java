package tools.datasync.pump;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

import tools.datasync.api.utils.SyncMessageQueue;

public class SyncPeerParms {

    private SyncPeer syncPeer;
    private SyncMessageQueue sendQueue;
    private SyncMessageQueue receiveQueue;

    private CountDownLatch ackPairReceiverLatch = new CountDownLatch(1);
    private CountDownLatch ackPeerSenderLatchA = new CountDownLatch(1);
    private CopyOnWriteArrayList<String> arrayList;

    public SyncPeerParms(SyncPeer syncPeer, SyncMessageQueue sendQueue,
	    SyncMessageQueue receiveQueue,
	    CopyOnWriteArrayList<String> arrayList) {
	this.syncPeer = syncPeer;
	this.sendQueue = sendQueue;
	this.receiveQueue = receiveQueue;
	this.arrayList = arrayList;
    }

    public SyncPeer getSyncPeer() {
	return syncPeer;
    }

    public SyncMessageQueue getSendQueue() {
	return sendQueue;
    }

    public SyncMessageQueue getReceiveQueue() {
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
