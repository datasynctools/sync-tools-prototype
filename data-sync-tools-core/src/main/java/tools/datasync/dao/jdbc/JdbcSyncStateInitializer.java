package tools.datasync.dao.jdbc;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.api.dao.EntityGetter;
import tools.datasync.api.dao.GenericDao;
import tools.datasync.api.dao.IdGetter;
import tools.datasync.api.msg.SyncEntityMessage;
import tools.datasync.api.utils.HashGenerator;
import tools.datasync.api.utils.Stringify;
import tools.datasync.dataformats.json.Jsonify;
import tools.datasync.model.DefaultSyncEntityMessage;
import tools.datasync.pump.SyncStateInitializer;
import tools.datasync.utils.Md5HashGenerator;
import tools.datasync.utils.StringUtils;

public class JdbcSyncStateInitializer implements SyncStateInitializer {

    private static final Logger LOG = LoggerFactory
	    .getLogger(JdbcSyncStateInitializer.class);

    private AtomicBoolean isRunning;
    private GenericDao genericDao;

    private List<String> tables;
    private EntityGetter entityGetter;
    private IdGetter idGetter;

    private Stringify stringify = new Jsonify();

    private HashGenerator hasher = Md5HashGenerator.getInstance();

    public JdbcSyncStateInitializer(List<String> tables,
	    EntityGetter entityGetter, IdGetter idGetter, GenericDao genericDao) {
	this.isRunning = new AtomicBoolean(false);
	this.tables = tables;
	this.entityGetter = entityGetter;
	this.idGetter = idGetter;
	this.genericDao = genericDao;
    }

    public List<String> getTables() {
	return tables;
    }

    public void doSeed() throws Exception {

	for (String table : tables) {

	    LOG.info("Populating SyncState table for [" + table + "] records");
	    Iterator<SyncEntityMessage> syncEntityMsgIterator = genericDao
		    .selectAll(table, true);

	    while (syncEntityMsgIterator.hasNext()) {
		SyncEntityMessage record = syncEntityMsgIterator.next();

		SyncEntityMessage syncState = new DefaultSyncEntityMessage();

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
	String recordString = stringify.toString(record);
	syncState.set("RECORDDATA", recordString);
	String hash = hasher.generate(recordString);
	syncState.set("RECORDHASH", hash);
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

    public Stringify getStringify() {
	return stringify;
    }

    public void setStringify(Stringify stringify) {
	this.stringify = stringify;
    }

    public HashGenerator getHasher() {
	return hasher;
    }

    public void setHasher(HashGenerator hasher) {
	this.hasher = hasher;
    }

    public String toString() {
	StringBuilder answer = new StringBuilder();
	answer.append(StringUtils.getSimpleName(this));
	answer.append("{");
	answer.append("tables=");
	answer.append(tables);
	answer.append(", ");
	answer.append("entityGetter=");
	answer.append(entityGetter);
	answer.append(", ");
	answer.append("idGetter=");
	answer.append(idGetter);
	answer.append(", ");
	answer.append("genericDao=");
	answer.append(genericDao);
	answer.append("}");
	return (answer.toString());
    }

}
