package tools.datasync.basic.sync.pump;

import java.util.concurrent.atomic.AtomicBoolean;

public class JvmSyncConcurArgs {

    private AtomicBoolean stopper;
    // private CountDownLatch beginSenderLatch;
    private NextEntityAwaiter nextEntityAwaiter;
    private NextEntitySignaler nextEntitySignaler;

    // public JvmSyncConcurArgs(AtomicBoolean stopper,
    // CountDownLatch beginSenderLatch,
    // NextEntityAwaiter nextEntityAwaiter,
    // NextEntitySignaler nextEntitySignaler) {
    // this.stopper = stopper;
    // //this.beginSenderLatch = beginSenderLatch;
    // this.nextEntityAwaiter = nextEntityAwaiter;
    // this.nextEntitySignaler = nextEntitySignaler;
    // }

    public JvmSyncConcurArgs(AtomicBoolean stopper,
	    NextEntityAwaiter nextEntityAwaiter,
	    NextEntitySignaler nextEntitySignaler) {
	this.stopper = stopper;
	// this.beginSenderLatch = beginSenderLatch;
	this.nextEntityAwaiter = nextEntityAwaiter;
	this.nextEntitySignaler = nextEntitySignaler;
    }

    public AtomicBoolean getStopper() {
	return stopper;
    }

    //
    // public CountDownLatch getBeginSenderLatch() {
    // return beginSenderLatch;
    // }

    public NextEntityAwaiter getNextEntityAwaiter() {
	return nextEntityAwaiter;
    }

    public NextEntitySignaler getNextEntitySignaler() {
	return nextEntitySignaler;
    }

}
