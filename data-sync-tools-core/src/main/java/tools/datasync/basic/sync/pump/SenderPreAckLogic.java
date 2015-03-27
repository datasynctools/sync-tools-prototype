package tools.datasync.basic.sync.pump;

import static tools.datasync.basic.comm.SyncMessageType.BEGIN_SEED;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.basic.comm.SyncMessage;
import tools.datasync.basic.util.StringUtils;

public class SenderPreAckLogic {

    private static final Logger LOG = LoggerFactory
	    .getLogger(SenderPreAckLogic.class);

    private SenderPresentAcknolwedger senderPresentAcknolwedger = new SenderPresentAcknolwedger();
    private ReceiverPresentAcknolwedger receiverPresentAcknolwedger = new ReceiverPresentAcknolwedger();
    private SyncStateInitializer syncStateInitializer;
    private BlockingQueue<SyncMessage> sendQueue;

    // TODO does the message number make sense? how is it used? think it's just
    // extraneous and should be removed
    public SenderPreAckLogicResult preAckMain(AtomicBoolean isRunning,
	    AtomicBoolean stopper, long messageNumber) throws SQLException,
	    IOException, InterruptedException {

	messageNumber = initialze(messageNumber);

	if (!receiverPresentAcknolwedger.waitForReceiverAck(isRunning)) {
	    return new SenderPreAckLogicResult(false, messageNumber);
	}

	LOG.info("The receiver for this sender is acknowledged");

	if (!senderPresentAcknolwedger.waitForSenderAck(isRunning, stopper)) {
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
	this.sendQueue.put(syncMessage);
	LOG.info("Completed seeding, so sent sync message of "
		+ "type {}, number {}", syncMessage.getMessageType(),
		syncMessage.getMessageNumber());
	return messageNumber;
    }

    private SyncMessage createSyncMessage(long messageNumber) {
	SyncMessage syncMessage = new SyncMessage();
	syncMessage.setMessageNumber(messageNumber);
	syncMessage.setMessageType(BEGIN_SEED);
	syncMessage.setTimestamp(System.currentTimeMillis());
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

    public void setAckPeerSenderLatch(CountDownLatch ackPeerSenderLatch) {
	senderPresentAcknolwedger.setAckPeerSenderLatch(ackPeerSenderLatch);
    }

    public void setSendQueue(BlockingQueue<SyncMessage> sendQueue) {
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
	answer.append("syncStateInitializer=");
	answer.append(syncStateInitializer.toString());
	answer.append("}");
	return (answer.toString());
    }

}
