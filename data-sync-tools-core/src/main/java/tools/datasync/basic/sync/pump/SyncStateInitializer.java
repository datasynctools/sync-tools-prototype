package tools.datasync.basic.sync.pump;

import java.util.List;

import tools.datasync.api.dao.EntityGetter;

public interface SyncStateInitializer {

    void doSeed() throws Exception;

    void setIsRunning(boolean isRunning);

    List<String> getTables();

    EntityGetter getEntityGetter();

}
