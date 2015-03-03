package tools.datasync.basic.sync.pump;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.basic.comm.SyncMessage;
import tools.datasync.basic.comm.SyncMessageType;
import tools.datasync.basic.util.JSONMapperBean;

public class SenderPreAckLogic {

    private static final Logger LOG = LoggerFactory
	    .getLogger(SenderPreAckLogic.class);

    private SenderPresentAcknolwedger senderPresentAcknolwedger = new SenderPresentAcknolwedger();
    private ReceiverPresentAcknolwedger receiverPresentAcknolwedger = new ReceiverPresentAcknolwedger();
    private BothSendersPresentAcknowledger bothSendersPresentAcknowledger = new BothSendersPresentAcknowledger();
    private SyncStateInitializer syncStateInitializer;
    private BlockingQueue<String> sendQueue;
    private JSONMapperBean jsonMapper;

    // TODO does the message number make sense? how is it used? think it's just
    // extraneous and should be removed
    public SenderPreAckLogicResult preAckMain(AtomicBoolean isRunning,
	    AtomicBoolean stopper, long messageNumber) throws SQLException,
	    IOException, InterruptedException {

	messageNumber = initialze(messageNumber);

	if (!receiverPresentAcknolwedger.waitForReceiverAck(isRunning)) {
	    return new SenderPreAckLogicResult(false, messageNumber);
	}

	if (!senderPresentAcknolwedger.waitForSenderAck(isRunning, stopper)) {
	    return new SenderPreAckLogicResult(false, messageNumber);

	}

	if (!bothSendersPresentAcknowledger.waitForBothSendersAck(isRunning,
		stopper)) {
	    return new SenderPreAckLogicResult(false, messageNumber);
	}

	return new SenderPreAckLogicResult(true, messageNumber);

    }

    private long initialze(long messageNumber) throws SQLException,
	    IOException, InterruptedException {
	syncStateInitializer.setIsRunning(true);
	syncStateInitializer.doSeed();
	syncStateInitializer.setIsRunning(false);

	// Send message to the other Peer's receiver "Begin seed"
	SyncMessage syncMessage = createSyncMessage(messageNumber++);
	// SyncMessage syncMessage = new SyncMessage(null, messageNumber++,
	// SyncMessageType.BEGIN_SEED.toString(), null, null,
	// System.currentTimeMillis());
	String message = jsonMapper.writeValueAsString(syncMessage);
	this.sendQueue.put(message);
	LOG.info("Completed seeding, send message to the peer that we're ready to receive");

	return messageNumber;
    }

    private SyncMessage createSyncMessage(long messageNumber) {
	SyncMessage syncMessage = new SyncMessage();
	// syncMessage.setOriginId(null); //on purpose null
	syncMessage.setMessageNumber(messageNumber);
	syncMessage.setMessageType(SyncMessageType.BEGIN_SEED.toString());
	syncMessage.setTimestamp(System.currentTimeMillis());
	// syncMessage.setPayloadHash(null); //on purpose null
	// syncMessage.setPayloadJson(null); //on purpose null
	return (syncMessage);
    }

    public void setSyncStateInitializer(
	    SyncStateInitializer syncStateInitializer) {
	this.syncStateInitializer = syncStateInitializer;
    }

    public void setAckPairReceiverLatch(CountDownLatch ackPairReceiverLatch) {
	receiverPresentAcknolwedger
		.setAckPairReceiverLatch(ackPairReceiverLatch);
    }

    public void setJsonMapper(JSONMapperBean jsonMapper) {
	this.jsonMapper = jsonMapper;
    }

    public void setAckPeerSenderLatch(CountDownLatch ackPeerSenderLatch) {
	senderPresentAcknolwedger.setAckPeerSenderLatch(ackPeerSenderLatch);
    }

    public void setBeginSenderLatch(CountDownLatch beginSenderLatch) {
	bothSendersPresentAcknowledger.setBeginSenderLatch(beginSenderLatch);
    }

    public void setSendQueue(BlockingQueue<String> sendQueue) {
	this.sendQueue = sendQueue;
    }

    public void setSenderPresentAcknolwedger(
	    SenderPresentAcknolwedger senderPresentAcknolwedger) {
	this.senderPresentAcknolwedger = senderPresentAcknolwedger;
    }

    public void setReceiverPresentAcknolwedger(
	    ReceiverPresentAcknolwedger receiverPresentAcknolwedger) {
	this.receiverPresentAcknolwedger = receiverPresentAcknolwedger;
    }

    public void setBothSendersPresentAcknowledger(
	    BothSendersPresentAcknowledger bothSendersPresentAcknowledger) {
	this.bothSendersPresentAcknowledger = bothSendersPresentAcknowledger;
    }

}
