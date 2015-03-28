package tools.datasync.pump;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SenderPresentAcknolwedger {

    private static final Logger LOG = LoggerFactory
	    .getLogger(SenderPresentAcknolwedger.class);

    private long awaitDuration = 500;
    private TimeUnit awaitUnit = TimeUnit.MILLISECONDS;

    private CountDownLatch ackPeerSenderLatch;

    public boolean waitForSenderAck(AtomicBoolean isRunning,
	    AtomicBoolean stopper) throws InterruptedException {

	LOG.info("Waiting for peer sender message");
	while (!ackPeerSenderLatch.await(awaitDuration, awaitUnit)) {
	    if (stopper.get()) {
		LOG.info("Stop requested, shutting down");
		isRunning.set(false);
		return false;
	    }
	    LOG.debug("No acknowledge received on waiting for ackPeerSenderLatch "
		    + ackPeerSenderLatch);
	}
	LOG.info("Peer sender sent 'begin seed', ready to send messages");
	return true;
    }

    public void setAckPeerSenderLatch(CountDownLatch ackPeerSenderLatch) {
	this.ackPeerSenderLatch = ackPeerSenderLatch;
    }

}
