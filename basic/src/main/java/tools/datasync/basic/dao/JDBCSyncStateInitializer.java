package tools.datasync.basic.dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import tools.datasync.basic.model.Ids;
import tools.datasync.basic.model.JSON;
import tools.datasync.basic.sync.pump.SyncStateInitializer;
import tools.datasync.basic.util.JSONMapperBean;
import tools.datasync.basic.util.Md5HashGenerator;

public class JDBCSyncStateInitializer implements SyncStateInitializer {

	private AtomicBoolean isRunning;
	private GenericDao genericDao = null;
	private JSONMapperBean jsonMapper;
	private Md5HashGenerator hashGenerator;
	
	Logger logger = Logger.getLogger(JDBCSyncStateInitializer.class.getName());
	String[] tables = { Ids.Table.CONTACT, Ids.Table.WORK_HISTORY, Ids.Table.CONTACT_LINK };
	
	public JDBCSyncStateInitializer() {
		this.isRunning = new AtomicBoolean(false);
		this.jsonMapper = JSONMapperBean.getInstance();
        this.hashGenerator = Md5HashGenerator.getInstance();
	}

	@Override
	public void doSeed() throws SQLException, IOException {
		
		for(int tab=0; tab<tables.length; tab++){
			
			logger.info("Populating SyncState table for ["+tables[tab]+"] records...");
			Iterator<JSON> jsonIterator = genericDao.selectAll(tables[tab]);
			
			while(jsonIterator.hasNext()){
				JSON record = jsonIterator.next();
				
				JSON syncState = new JSON(Ids.Table.SYNC_STATE);
				syncState.set("EntityId", Ids.EntityId.get(tables[tab]));
				syncState.set("RecordId", record.get(Ids.KeyColumn.get(tables[tab])));
				String recordJson = jsonMapper.writeValueAsString(record);
				syncState.set("RecordData", recordJson);
				syncState.set("RecordHash", hashGenerator.generate(recordJson));
				
				genericDao.save(Ids.Table.SYNC_STATE, syncState);
			}
		}
	}

	@Override
	public void setIsRunning(boolean isRunning) {
		this.isRunning.set(isRunning);
	}

	@Override
	public void setGenericDao(GenericDao genericDao) {
		this.genericDao = genericDao;
	}

}
