package tools.datasync.pump;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReceiverPresentAcknolwedger {

    private static final Logger LOG = LoggerFactory
	    .getLogger(ReceiverPresentAcknolwedger.class);

    private long awaitDuration = 500;
    private TimeUnit awaitUnit = TimeUnit.MILLISECONDS;

    private CountDownLatch ackPairReceiverLatch;

    public boolean waitForReceiverAck(AtomicBoolean isRunning)
	    throws InterruptedException {
	// Confirm pair receiver is up-and-running
	while (!ackPairReceiverLatch.await(awaitDuration, awaitUnit)) {
	    LOG.info("Stop requested, shutting down");
	    isRunning.set(false);
	    return false;
	}
	return true;
    }

    public void setAckPairReceiverLatch(CountDownLatch ackPairReceiverLatch) {
	this.ackPairReceiverLatch = ackPairReceiverLatch;
    }

}
