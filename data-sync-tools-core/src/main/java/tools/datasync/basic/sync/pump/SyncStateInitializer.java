package tools.datasync.basic.sync.pump;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface SyncStateInitializer {

    void doSeed() throws SQLException, IOException;

    void setIsRunning(boolean isRunning);

    List<String> getTables();

    // void setGenericDao(GenericDao genericDao);

}
