package tools.datasync.basic.sync.pump.handlers;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.basic.comm.SyncMessage;

public class BeginSeedProcessor implements SyncMessageProcessor {

    private static final Logger LOG = LoggerFactory
	    .getLogger(BeginSeedProcessor.class);

    private CountDownLatch latch;

    public BeginSeedProcessor(CountDownLatch latch) {
	this.latch = latch;
    }

    @Override
    public boolean handle(SyncMessage syncMessage) {

	latch.countDown();
	LOG.info("Acknowledged sender can start sending {}", latch);

	return false; // not finished
    }

}
