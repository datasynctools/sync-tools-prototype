package tools.datasync.pump;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import tools.datasync.api.msg.SyncMessage;
import tools.datasync.seed.SeedProducer;

public class JvmSyncPumpSenderSupport {

    SenderPreAckLogic senderPreAckLogic = new SenderPreAckLogic();
    SenderPostAckLogic senderPostAckLogic = new SenderPostAckLogic();

    private AtomicBoolean isRunning;
    private AtomicBoolean stopper;
    private long messageNumber = 0;

    public void initialize(BlockingQueue<SyncMessage> sendQueue,
	    SyncStateInitializer syncStateInitializer,
	    JvmSyncConcurArgs concurArgs) {

	this.stopper = concurArgs.getStopper();
	this.isRunning = new AtomicBoolean(false);

	senderPreAckLogic.setSendQueue(sendQueue);
	senderPreAckLogic.setSyncStateInitializer(syncStateInitializer);

	senderPostAckLogic.setIsRunning(isRunning);
	senderPostAckLogic.setStopper(stopper);
	senderPostAckLogic.setSendQueue(sendQueue);
	senderPostAckLogic.setNextEntityAwaiter(concurArgs
		.getNextEntityAwaiter());
    }

    public void runMain() throws Exception {

	SenderPreAckLogicResult result = senderPreAckLogic.preAckMain(
		isRunning, stopper, messageNumber);

	if (!result.isContinueProcessing()) {
	    return;
	}

	messageNumber = result.getMessageCount();

	messageNumber = senderPostAckLogic.runPostAckMain(messageNumber);
    }

    public void stop() {
	stopper.set(true);
	isRunning.set(false);
    }

    public AtomicBoolean isRunning() {
	return isRunning;
    }

    public void setAckPairReceiverLatch(CountDownLatch ackPairReceiverLatch) {
	senderPreAckLogic.setAckPairReceiverLatch(ackPairReceiverLatch);
    }

    public void setAckPeerSenderLatch(CountDownLatch ackPeerSenderLatch) {
	senderPreAckLogic.setAckPeerSenderLatch(ackPeerSenderLatch);
    }

    public void setSeedProducer(SeedProducer seedProducer) {
	senderPostAckLogic.setSeedProducer(seedProducer);
    }

}
