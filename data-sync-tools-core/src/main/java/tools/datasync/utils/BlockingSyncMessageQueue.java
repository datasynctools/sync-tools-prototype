package tools.datasync.utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import tools.datasync.api.msg.SyncMessage;
import tools.datasync.api.utils.SyncMessageQueue;

public class BlockingSyncMessageQueue implements SyncMessageQueue {

    private BlockingQueue<SyncMessage> queue;

    public BlockingSyncMessageQueue(BlockingQueue<SyncMessage> queue) {
	this.queue = queue;
    }

    public void put(SyncMessage syncMessage) {
	try {
	    queue.put(syncMessage);
	} catch (InterruptedException e) {
	    throw (new RuntimeException(e));
	}
    }

    public SyncMessage poll(long timeout, TimeUnit unit) {
	SyncMessage syncMessage;
	try {
	    syncMessage = queue.poll(timeout, unit);
	} catch (InterruptedException e) {
	    throw (new RuntimeException(e));
	}
	return (syncMessage);
    }

}
