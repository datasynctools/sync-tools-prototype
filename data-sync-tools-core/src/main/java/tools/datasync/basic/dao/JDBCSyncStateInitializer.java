package tools.datasync.basic.dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import tools.datasync.basic.model.EntityGetter;
import tools.datasync.basic.model.IdGetter;
import tools.datasync.basic.model.JSON;
import tools.datasync.basic.sync.pump.SyncStateInitializer;
import tools.datasync.basic.util.JSONMapperBean;

public class JDBCSyncStateInitializer implements SyncStateInitializer {

    private AtomicBoolean isRunning;
    private GenericDao genericDao;
    private JSONMapperBean jsonMapper;
    // private Md5HashGenerator hashGenerator;

    Logger logger = Logger.getLogger(JDBCSyncStateInitializer.class.getName());
    private List<String> tables;
    private EntityGetter entityGetter;
    private IdGetter idGetter;

    // = { Ids.Table.CONTACT, Ids.Table.WORK_HISTORY,
    // Ids.Table.CONTACT_LINK };

    public JDBCSyncStateInitializer(List<String> tables,
	    EntityGetter entityGetter, IdGetter idGetter, GenericDao genericDao) {
	this.isRunning = new AtomicBoolean(false);
	this.jsonMapper = JSONMapperBean.getInstance();
	this.tables = tables;
	this.entityGetter = entityGetter;
	this.idGetter = idGetter;
	this.genericDao = genericDao;
	// this.hashGenerator = Md5HashGenerator.getInstance();
    }

    public List<String> getTables() {
	return tables;
    }

    // @Override
    public void doSeed() throws SQLException, IOException {

	for (String table : tables) {

	    logger.info("Populating SyncState table for [" + table
		    + "] records...");
	    Iterator<JSON> jsonIterator = genericDao.selectAll(table, true);

	    while (jsonIterator.hasNext()) {
		JSON record = jsonIterator.next();

		JSON syncState = new JSON(table);
		syncState.set("ENTITYID", idGetter.get(table));
		syncState.set("RECORDID", record.getCalculatedPrimaryKey());
		String recordJson = jsonMapper.writeValueAsString(record);
		syncState.set("RECORDDATA", recordJson);
		syncState.set("RECORDHASH", record.generateHash());

		genericDao.save(entityGetter.getSyncStateName(), syncState);
	    }
	}
    }

    // @Override
    public void setIsRunning(boolean isRunning) {
	this.isRunning.set(isRunning);
    }

    // @Override
    public void setGenericDao(GenericDao genericDao) {
	this.genericDao = genericDao;
    }

    public EntityGetter getEntityGetter() {
	return entityGetter;
    }

}
