package tools.datasync.basic.dao;

import java.util.concurrent.atomic.AtomicBoolean;

import tools.datasync.basic.sync.pump.SyncStateInitializer;

public class JDBCSyncStateInitializer implements SyncStateInitializer {

    private AtomicBoolean isRunning;

    @Override
    public void doSeed() {
	// TODO Auto-generated method stub

    }

    @Override
    public void setIsRunning(AtomicBoolean isRunning) {
	this.isRunning = isRunning;
    }

}
