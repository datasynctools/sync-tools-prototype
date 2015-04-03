package tools.datasync.pump;


public class SyncFutureResult {

    private SyncPump pumpA2B;
    private SyncPump pumpB2A;

    public SyncFutureResult(SyncPump pumpA2B, SyncPump pumpB2A) {
	this.pumpA2B = pumpA2B;
	this.pumpB2A = pumpB2A;
    }

    boolean isRunning() {
	return (pumpA2B.isPumping() && pumpB2A.isPumping());
    }

}
