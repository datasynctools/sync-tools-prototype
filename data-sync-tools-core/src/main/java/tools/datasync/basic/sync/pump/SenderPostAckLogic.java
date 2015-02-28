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
import tools.datasync.basic.util.JSONMapperBean;

public class SenderPostAckLogic {

    private static final Logger LOG = LoggerFactory
	    .getLogger(SenderPostAckLogic.class);

    private BlockingQueue<String> sendQueue;
    private JSONMapperBean jsonMapper;
    private SeedProducer seedProducer;

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
	syncMessage = new SyncMessage(null, messageNumber++,
		SyncMessageType.SYNC_OVER.toString(), null, null,
		System.currentTimeMillis());
	message = jsonMapper.writeValueAsString(syncMessage);

	LOG.info("Sending - " + message);
	this.sendQueue.put(message);
	return messageNumber;
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
	String paloadHash = seed.getRecordHash();
	syncMessage = new SyncMessage(seed.getOrigin(), messageNumber++,
		SyncMessageType.SEED.toString(), payloadJson, paloadHash,
		System.currentTimeMillis());
	message = jsonMapper.writeValueAsString(syncMessage);

	LOG.info("Sending - " + message);
	this.sendQueue.put(message);
	return (messageNumber);

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

	    messageNumber = sendSyncMessage(seed, syncMessage, message,
		    messageNumber);

	}
	return (messageNumber);

    }

    public void setJsonMapper(JSONMapperBean jsonMapper) {
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

}
