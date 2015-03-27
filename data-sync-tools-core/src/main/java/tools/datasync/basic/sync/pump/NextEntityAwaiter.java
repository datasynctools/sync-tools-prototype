package tools.datasync.basic.sync.pump;

import static tools.datasync.api.msg.SyncMessageType.PEER_READY_WITH_NEXT_ENTITY;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.api.msg.SyncMessage;
import tools.datasync.basic.model.EnityId;
import tools.datasync.basic.model.SeedRecord;
import tools.datasync.basic.util.StringUtils;
import tools.datasync.basic.util.TimeSpan;

public class NextEntityAwaiter {

    private static final Logger LOG = LoggerFactory
	    .getLogger(NextEntityAwaiter.class);

    private String lastEntityId = null;
    private CopyOnWriteArrayList<String> arrayList;
    private TimeSpan awaitTimeSpan;
    private AtomicBoolean stopper;
    private BlockingQueue<SyncMessage> sendQueue;

    public NextEntityAwaiter(CopyOnWriteArrayList<String> arrayList,
	    TimeSpan awaitTimeSpan, BlockingQueue<SyncMessage> sendQueue,
	    AtomicBoolean stopper) {
	this.arrayList = arrayList;
	this.awaitTimeSpan = awaitTimeSpan;
	this.sendQueue = sendQueue;
	this.stopper = stopper;
    }

    public long awaitForNextEntity(SeedRecord seed, long messageNumber) {
	if (lastEntityId == null) {
	    lastEntityId = seed.getEntityId();
	    // first time processing, so no need to await
	    return messageNumber;
	}
	String thisEntityId = seed.getEntityId();
	if (lastEntityId.equals(thisEntityId)) {
	    // we're processing the same entity, so no need to await
	    return messageNumber;
	}

	long localMessageNumber = sendSyncMessage(lastEntityId, messageNumber);

	await(lastEntityId);

	lastEntityId = thisEntityId;

	return (localMessageNumber);
    }

    private long sendSyncMessage(String entityId, long messageNumber) {

	messageNumber = messageNumber + 1;
	try {
	    SyncMessage syncMessage = createSyncMessage(entityId, messageNumber);

	    LOG.info("Signal to peer that I have finished an "
		    + "Entity set with value {} using {}[{}]", lastEntityId,
		    sendQueue.getClass(), sendQueue.hashCode());

	    sendQueue.put(syncMessage);
	} catch (Exception e) {
	    throw (new RuntimeException(e));
	}

	return messageNumber;
    }

    private SyncMessage createSyncMessage(String thisEntityId,
	    long messageNumber) {
	SyncMessage syncMessage = new SyncMessage();
	syncMessage.setMessageNumber(messageNumber);
	syncMessage.setMessageType(PEER_READY_WITH_NEXT_ENTITY);
	syncMessage.setTimestamp(System.currentTimeMillis());
	EnityId entityIdObj = new EnityId();
	entityIdObj.setEntityId(thisEntityId);
	syncMessage.setPayloadData(entityIdObj);
	return (syncMessage);
    }

    private void await(String entityId) {
	boolean firstRun = true;
	do {
	    if (arrayList.contains(entityId)) {

		LOG.info("Entity {} acknowledged {}", entityId, arrayList);
		lastEntityId = entityId;

		// entity found, so no need to await
		return;
	    }

	    // TODO: Need an algorithm with signaling that indicates that the
	    // other side is no longer available for communication. We can't
	    // just wait forever.
	    if (firstRun) {
		LOG.info(
			"Waiting for entity to finished processing entity Id {} from peer before continuing (current cache: {})",
			entityId, arrayList);
		firstRun = false;
	    }

	} while (sleep());
    }

    private boolean sleep() {
	try {
	    awaitTimeSpan.getUnit().sleep(awaitTimeSpan.getDuration());
	    if (stopper != null && stopper.get()) {
		// TODO: this is probably not the best way to stop this.
		throw (new RuntimeException(
			"Stop request issued, stopping sender with exception from Next Entity Awaiter"));
	    }
	} catch (InterruptedException e) {
	    throw (new RuntimeException(e));
	}
	return true;
    }

    private void addQueues(StringBuilder answer) {
	answer.append("sendQueue=");
	answer.append(sendQueue.toString());
	answer.append(", ");
	answer.append("sendQueueClass=");
	answer.append(StringUtils.getSimpleName(sendQueue));
	answer.append(", ");
	answer.append("queueInstanceHashCode=");
	answer.append(sendQueue.hashCode());
    }

    public String toString() {
	StringBuilder answer = new StringBuilder();
	answer.append(StringUtils.getSimpleName(this));
	answer.append("{");
	addQueues(answer);
	answer.append("}");
	return (answer.toString());
    }

}
