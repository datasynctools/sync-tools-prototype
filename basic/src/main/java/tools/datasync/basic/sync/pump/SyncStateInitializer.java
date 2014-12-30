package tools.datasync.basic.sync.pump;

import java.util.concurrent.atomic.AtomicBoolean;

public interface SyncStateInitializer {

    void doSeed();

    void setIsRunning(AtomicBoolean isRunning);

}
