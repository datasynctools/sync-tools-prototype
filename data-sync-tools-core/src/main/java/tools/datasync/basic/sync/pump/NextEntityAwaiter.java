package tools.datasync.basic.sync.pump;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.basic.comm.SyncMessage;
import tools.datasync.basic.comm.SyncMessageType;
import tools.datasync.basic.model.SeedRecord;
import tools.datasync.basic.util.JsonMapperBean;
import tools.datasync.basic.util.TimeSpan;

public class NextEntityAwaiter {

    private static final Logger LOG = LoggerFactory
	    .getLogger(NextEntityAwaiter.class);

    private String lastEntityId = null;
    private CopyOnWriteArrayList<String> arrayList;
    private TimeSpan awaitTimeSpan;
    private AtomicBoolean stopper;
    private BlockingQueue<String> sendQueue;
    private JsonMapperBean jsonMapper = JsonMapperBean.getInstance();

    public NextEntityAwaiter(CopyOnWriteArrayList<String> arrayList,
	    TimeSpan awaitTimeSpan, BlockingQueue<String> sendQueue,
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

	    String message = jsonMapper.writeValueAsString(syncMessage);

	    LOG.info("Signal to peer that I have finished an "
		    + "Entity set with value {} using {}[{}]", lastEntityId,
		    sendQueue.getClass(), sendQueue.hashCode());

	    sendQueue.put(message);
	} catch (Exception e) {
	    throw (new RuntimeException(e));
	}

	return messageNumber;
    }

    private SyncMessage createSyncMessage(String thisEntityId,
	    long messageNumber) {
	SyncMessage syncMessage = new SyncMessage();
	// syncMessage.setOriginId(null); //leaving null on purpose
	syncMessage.setMessageNumber(messageNumber);
	syncMessage.setMessageType(SyncMessageType.PEER_READY_WITH_NEXT_ENTITY
		.toString());
	syncMessage.setTimestamp(System.currentTimeMillis());
	// syncMessage.setPaloadHash(null);//leaving null on purpose
	syncMessage.setPayloadJson(thisEntityId);
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

    public void setJsonMapper(JsonMapperBean jsonMapper) {
	this.jsonMapper = jsonMapper;
    }

}
