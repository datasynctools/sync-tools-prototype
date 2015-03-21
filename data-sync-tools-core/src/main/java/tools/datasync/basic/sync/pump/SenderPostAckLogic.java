package tools.datasync.basic.sync.pump;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.basic.comm.SyncMessage;
import tools.datasync.basic.comm.SyncMessageType;
import tools.datasync.basic.model.SeedRecord;
import tools.datasync.basic.seed.SeedException;
import tools.datasync.basic.seed.SeedOverException;
import tools.datasync.basic.seed.SeedProducer;
import tools.datasync.basic.util.JsonMapperBean;

public class SenderPostAckLogic {

    private static final Logger LOG = LoggerFactory
	    .getLogger(SenderPostAckLogic.class);

    private BlockingQueue<String> sendQueue;
    private JsonMapperBean jsonMapper;
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

	LOG.info("Flag state: Thread interrupted="
		+ Thread.currentThread().isInterrupted() + ", isRunning="
		+ isRunning.get() + ", stopper=" + stopper.get()
		+ ", seedProducer.isRunning=" + seedProducer.isRunning());
	syncMessage = createSyncMessage(messageNumber++);

	message = jsonMapper.writeValueAsString(syncMessage);

	LOG.info("Sending - " + message);
	this.sendQueue.put(message);
	return messageNumber;
    }

    private SyncMessage createSyncMessage(long messageNumber) {
	SyncMessage syncMessage = new SyncMessage();
	// syncMessage.setOriginId(null); //leaving null on purpose
	syncMessage.setMessageNumber(messageNumber);
	syncMessage.setMessageType(SyncMessageType.SYNC_OVER.toString());
	syncMessage.setTimestamp(System.currentTimeMillis());
	// syncMessage.setPaloadHash(null);//leaving null on purpose
	// syncMessage.setPayloadJson(null); //leaving null on purpose
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
	String payloadJson = jsonMapper.writeValueAsString(seed);
	String payloadHash = seed.getRecordHash();

	syncMessage = createSyncMessage(seed, messageNumber, payloadJson,
		payloadHash);

	message = jsonMapper.writeValueAsString(syncMessage);

	LOG.info("Sending - " + message);
	this.sendQueue.put(message);
	return (messageNumber + 1);

    }

    private SyncMessage createSyncMessage(SeedRecord seed, long messageNumber,
	    String payloadJson, String payloadHash) {
	SyncMessage syncMessage = new SyncMessage();
	syncMessage.setOriginId(seed.getOrigin());
	syncMessage.setMessageNumber(messageNumber);
	syncMessage.setMessageType(SyncMessageType.SEED.toString());
	syncMessage.setTimestamp(System.currentTimeMillis());
	syncMessage.setPayloadHash(payloadHash);
	syncMessage.setPayloadJson(payloadJson);
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
		LOG.info(">>> Seed phase is over... Terminating the sender process logic.");
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

    public void setJsonMapper(JsonMapperBean jsonMapper) {
	this.jsonMapper = jsonMapper;
    }

    public void setSendQueue(BlockingQueue<String> sendQueue) {
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

}
