package tools.datasync.basic.sync.pump;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class JvmSyncConcurArgs {

    private AtomicBoolean stopper;
    private CountDownLatch beginSenderLatch;

    public JvmSyncConcurArgs(AtomicBoolean stopper,
	    CountDownLatch beginSenderLatch) {
	this.stopper = stopper;
	this.beginSenderLatch = beginSenderLatch;
    }

    public AtomicBoolean getStopper() {
	return stopper;
    }

    public CountDownLatch getBeginSenderLatch() {
	return beginSenderLatch;
    }

}
