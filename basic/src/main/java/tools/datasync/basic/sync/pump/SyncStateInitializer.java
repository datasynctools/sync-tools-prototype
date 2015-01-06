package tools.datasync.basic.sync.pump;

import java.io.IOException;
import java.sql.SQLException;

import tools.datasync.basic.dao.GenericDao;


public interface SyncStateInitializer {

    void doSeed() throws SQLException, IOException;

    void setIsRunning(boolean isRunning);

	void setGenericDao(GenericDao genericDao);

}
