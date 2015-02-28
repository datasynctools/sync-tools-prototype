package tools.datasync.basic.sync.pump;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BothSendersPresentAcknowledger {

    private static final Logger LOG = LoggerFactory
	    .getLogger(BothSendersPresentAcknowledger.class);

    private long awaitDuration = 500;
    private TimeUnit awaitUnit = TimeUnit.MILLISECONDS;

    private CountDownLatch beginSenderLatch;

    public boolean waitForBothSendersAck(AtomicBoolean isRunning,
	    AtomicBoolean stopper) throws InterruptedException {
	// Count down beginSenderLatch indicating this sender is ready.
	LOG.info(">>> Counting down beginSenderLatch indicating this sender is ready");
	beginSenderLatch.countDown();

	// Wait on beginSenderLatch until both senders are ready.
	LOG.info(">>> Waiting on beginSenderLatch until both senders are ready");
	while (!beginSenderLatch.await(awaitDuration, awaitUnit)) {
	    if (stopper.get()) {
		LOG.info("Stop requested, shutting down");
		isRunning.set(false);
		return false;
	    }
	    LOG.info("No acknowledge received on waiting for beginSeedLatch "
		    + beginSenderLatch);
	}
	LOG.info("Both Senders acknowledged");
	return true;
    }

    public void setBeginSenderLatch(CountDownLatch beginSenderLatch) {
	this.beginSenderLatch = beginSenderLatch;
    }

}
