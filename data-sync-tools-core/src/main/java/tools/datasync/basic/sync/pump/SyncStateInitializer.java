package tools.datasync.basic.sync.pump;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import tools.datasync.api.dao.EntityGetter;

public interface SyncStateInitializer {

    void doSeed() throws SQLException, IOException;

    void setIsRunning(boolean isRunning);

    List<String> getTables();

    EntityGetter getEntityGetter();

}
