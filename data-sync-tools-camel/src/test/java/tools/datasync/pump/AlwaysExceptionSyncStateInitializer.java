package tools.datasync.pump;

import java.util.List;

import tools.datasync.api.dao.EntityGetter;

public class AlwaysExceptionSyncStateInitializer implements
	SyncStateInitializer {

    public void doSeed() throws Exception {
	throw (new RuntimeException("stop me on purpose"));
	// TODO Auto-generated method stub

    }

    public void setIsRunning(boolean isRunning) {
	// TODO Auto-generated method stub

    }

    public List<String> getTables() {
	// TODO Auto-generated method stub
	return null;
    }

    public EntityGetter getEntityGetter() {
	// TODO Auto-generated method stub
	return null;
    }

}
