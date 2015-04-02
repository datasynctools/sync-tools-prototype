package tools.datasync.pump;

import static tools.datasync.api.msg.SyncMessageType.SEED;
import static tools.datasync.api.msg.SyncMessageType.SYNC_OVER;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.api.msg.SyncMessage;
import tools.datasync.api.utils.SyncMessageQueue;
import tools.datasync.model.SeedRecord;
import tools.datasync.seed.SeedException;
import tools.datasync.seed.SeedOverException;
import tools.datasync.seed.SeedProducer;
import tools.datasync.utils.StringUtils;

public class SenderPostAckLogic {

    private static final Logger LOG = LoggerFactory
	    .getLogger(SenderPostAckLogic.class);

    private SyncMessageQueue sendQueue;
    private SeedProducer seedProducer;

    private NextEntityAwaiter nextEntityAwaiter;

    private AtomicBoolean isRunning;
    private AtomicBoolean stopper;

    // TODO does the message number make sense? how is it used? think it's just
    // extraneous and should be removed
    public long runPostAckMain(long messageNumber) throws SQLException,
	    IOException, InterruptedException, SeedOverException, SeedException {
	SyncMessage syncMessage = null;
	String message = null;

	messageNumber = sendSyncMessages(syncMessage, message, messageNumber);

	LOG.debug("Flag state: Thread interrupted="
		+ Thread.currentThread().isInterrupted() + ", isRunning="
		+ isRunning.get() + ", stopper=" + stopper.get()
		+ ", seedProducer.isRunning=" + seedProducer.isRunning());
	syncMessage = createSyncMessage(messageNumber++);

	LOG.info("Completed send processing, so sent final sync message of "
		+ "type {}, number {}", syncMessage.getMessageType(),
		syncMessage.getMessageNumber());

	this.sendQueue.put(syncMessage);
	return messageNumber;
    }

    private SyncMessage createSyncMessage(long messageNumber) {
	SyncMessage syncMessage = new SyncMessage();
	syncMessage.setMessageNumber(messageNumber);
	syncMessage.setMessageType(SYNC_OVER);
	syncMessage.setTimestamp(System.currentTimeMillis());
	return (syncMessage);
    }

    private boolean activeMessages() {
	boolean active = (!Thread.currentThread().isInterrupted())
		&& isRunning.get() && !stopper.get()
		&& seedProducer.isRunning();
	return (active);
    }

    private long sendSyncMessage(SeedRecord seed, SyncMessage syncMessage,
	    String message, long messageNumber) throws InterruptedException,
	    JsonGenerationException, JsonMappingException, IOException {
	String payloadHash = seed.getRecordHash();

	syncMessage = createSyncMessage(seed, messageNumber, seed, payloadHash);

	this.sendQueue.put(syncMessage);
	LOG.info("Sent sync message of type {}, number {}, entityId {}, "
		+ "recordId {}, and hash {}", syncMessage.getMessageType(),
		syncMessage.getMessageNumber(), seed.getEntityId(),
		seed.getRecordId(), seed.getRecordHash());
	return (messageNumber + 1);

    }

    private SyncMessage createSyncMessage(SeedRecord seed, long messageNumber,
	    SeedRecord payloadData, String payloadHash) {
	SyncMessage syncMessage = new SyncMessage();
	syncMessage.setOriginId(seed.getOrigin());
	syncMessage.setMessageNumber(messageNumber);
	syncMessage.setMessageType(SEED);
	syncMessage.setTimestamp(System.currentTimeMillis());
	syncMessage.setPayloadHash(payloadHash);
	syncMessage.setPayloadData(payloadData);
	return (syncMessage);
    }

    private long sendSyncMessages(SyncMessage syncMessage, String message,
	    long messageNumber) throws SeedOverException, SeedException,
	    JsonGenerationException, JsonMappingException, IOException,
	    InterruptedException {

	while (activeMessages()) {

	    // Get next seed
	    SeedRecord seed = seedProducer.getNextSeed();
	    if (seed == null) {
		LOG.debug("Seed phase is complete");
		isRunning.set(false);
		return messageNumber;
	    }

	    messageNumber = nextEntityAwaiter.awaitForNextEntity(seed,
		    messageNumber);

	    messageNumber = sendSyncMessage(seed, syncMessage, message,
		    messageNumber);

	}
	return (messageNumber);

    }

    public void setSendQueue(SyncMessageQueue sendQueue) {
	this.sendQueue = sendQueue;
    }

    public void setSeedProducer(SeedProducer seedProducer) {
	this.seedProducer = seedProducer;
    }

    public void setIsRunning(AtomicBoolean isRunning) {
	this.isRunning = isRunning;
    }

    public void setStopper(AtomicBoolean stopper) {
	this.stopper = stopper;
    }

    public void setNextEntityAwaiter(NextEntityAwaiter nextEntityAwaiter) {
	this.nextEntityAwaiter = nextEntityAwaiter;
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
	answer.append(", ");
    }

    public String toString() {
	StringBuilder answer = new StringBuilder();
	answer.append(StringUtils.getSimpleName(this));
	answer.append("{");
	addQueues(answer);
	answer.append("seedProducer=");
	answer.append(seedProducer.toString());
	answer.append(", ");
	answer.append("nextEntityAwaiter=");
	answer.append(nextEntityAwaiter.toString());
	answer.append("}");
	return (answer.toString());
    }

}
