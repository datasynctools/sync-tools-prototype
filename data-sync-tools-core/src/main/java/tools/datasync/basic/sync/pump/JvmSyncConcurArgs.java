package tools.datasync.basic.sync.pump;

import java.util.concurrent.atomic.AtomicBoolean;

public class JvmSyncConcurArgs {

    private AtomicBoolean stopper;
    private NextEntityAwaiter nextEntityAwaiter;
    private NextEntitySignaler nextEntitySignaler;

    public JvmSyncConcurArgs(AtomicBoolean stopper,
	    NextEntityAwaiter nextEntityAwaiter,
	    NextEntitySignaler nextEntitySignaler) {
	this.stopper = stopper;
	this.nextEntityAwaiter = nextEntityAwaiter;
	this.nextEntitySignaler = nextEntitySignaler;
    }

    public AtomicBoolean getStopper() {
	return stopper;
    }

    public NextEntityAwaiter getNextEntityAwaiter() {
	return nextEntityAwaiter;
    }

    public NextEntitySignaler getNextEntitySignaler() {
	return nextEntitySignaler;
    }

}
