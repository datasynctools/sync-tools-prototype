package tools.datasync.basic.dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.basic.model.EntityGetter;
import tools.datasync.basic.model.IdGetter;
import tools.datasync.basic.model.SyncEntityMessage;
import tools.datasync.basic.sync.pump.SyncStateInitializer;
import tools.datasync.basic.util.JsonMapperBean;

public class JdbcSyncStateInitializer implements SyncStateInitializer {

    private static final Logger LOG = LoggerFactory
	    .getLogger(JdbcSyncStateInitializer.class);

    private AtomicBoolean isRunning;
    private GenericDao genericDao;
    private JsonMapperBean jsonMapper;

    private List<String> tables;
    private EntityGetter entityGetter;
    private IdGetter idGetter;

    public JdbcSyncStateInitializer(List<String> tables,
	    EntityGetter entityGetter, IdGetter idGetter, GenericDao genericDao) {
	this.isRunning = new AtomicBoolean(false);
	this.jsonMapper = JsonMapperBean.getInstance();
	this.tables = tables;
	this.entityGetter = entityGetter;
	this.idGetter = idGetter;
	this.genericDao = genericDao;
    }

    public List<String> getTables() {
	return tables;
    }

    public void doSeed() throws SQLException, IOException {

	for (String table : tables) {

	    LOG.info("Populating SyncState table for [" + table + "] records");
	    Iterator<SyncEntityMessage> jsonIterator = genericDao.selectAll(
		    table, true);

	    while (jsonIterator.hasNext()) {
		SyncEntityMessage record = jsonIterator.next();

		SyncEntityMessage syncState = new SyncEntityMessage();

		// TODO Doug comment: I don't understand these variable names
		fillSyncEntityMessage(syncState, record, table);

		genericDao.save(entityGetter.getSyncStateName(), syncState);
	    }
	}
    }

    private void fillSyncEntityMessage(SyncEntityMessage syncState,
	    SyncEntityMessage record, String table)
	    throws JsonGenerationException, JsonMappingException, IOException {
	syncState.setEntity(table);
	syncState.set("ENTITYID", idGetter.get(table));
	syncState.set("RECORDID", record.getCalculatedPrimaryKey());
	String recordJson = jsonMapper.writeValueAsString(record);
	syncState.set("RECORDDATA", recordJson);
	syncState.set("RECORDHASH", record.generateHash());
    }

    public void setIsRunning(boolean isRunning) {
	this.isRunning.set(isRunning);
    }

    public void setGenericDao(GenericDao genericDao) {
	this.genericDao = genericDao;
    }

    public EntityGetter getEntityGetter() {
	return entityGetter;
    }

}
