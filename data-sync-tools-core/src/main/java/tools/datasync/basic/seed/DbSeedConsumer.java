/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * @author  Upendra Jariya
 * @sponsor Douglas Johnson
 * @version 1.0
 * @since   2014-11-10
 */
package tools.datasync.basic.seed;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.api.dao.EntityGetter;
import tools.datasync.api.dao.GenericDao;
import tools.datasync.api.dao.IdGetter;
import tools.datasync.api.msg.SyncEntityMessage;
import tools.datasync.api.utils.HashGenerator;
import tools.datasync.api.utils.Stringify;
import tools.datasync.basic.logic.ConflictResolver;
import tools.datasync.basic.model.DefaultSyncEntityMessage;
import tools.datasync.basic.model.SeedRecord;
import tools.datasync.dataformats.json.Jsonify;
import tools.datasync.utils.Md5HashGenerator;
import tools.datasync.utils.ObjectMapperFactory;
import tools.datasync.utils.StringUtils;

public class DbSeedConsumer implements SeedConsumer {

    private static final Logger LOG = LoggerFactory
	    .getLogger(DbSeedConsumer.class);

    private ObjectMapper jsonMapper = ObjectMapperFactory.getInstance();
    private HashGenerator hasher = Md5HashGenerator.getInstance();
    private GenericDao genericDao;
    private ConflictResolver conflictResolver;
    private EntityGetter entityGetter;
    private IdGetter recordIdGetter;
    private Stringify stringify = new Jsonify();

    public DbSeedConsumer(ConflictResolver conflictResolver,
	    EntityGetter entityGetter, IdGetter recordIdGetter,
	    GenericDao genericDao) {
	this.conflictResolver = conflictResolver;
	this.entityGetter = entityGetter;
	this.recordIdGetter = recordIdGetter;
	this.genericDao = genericDao;
    }

    private void validate(SeedRecord seed, SyncEntityMessage recordData) {

	String recordString = stringify.toString(recordData);

	if (!hasher.validate(recordString, seed.getRecordHash())) {

	    // TODO Is this the right logic? Should we throw an exception or
	    // some other logic?
	    LOG.warn("Illegal message - Record does not match with its hash\n"
		    + recordData);
	}
    }

    public void consume(SeedRecord seed) throws IOException, SeedException {

	LOG.debug("Consuming SEED Record: {}", seed);

	String entityName = entityGetter.getName(seed.getEntityId());
	SyncEntityMessage recordData = seed.getRecordData();

	validate(seed, recordData);

	try {
	    handle(seed, recordData, entityName);

	} catch (Exception e) {
	    LOG.error("Error while consuming record [" + seed + "]", e);
	    throw new IOException(e.getMessage(), e);
	}

    }

    private void handle(SeedRecord seed, SyncEntityMessage recordData,
	    String entityName) throws Exception {
	SyncEntityMessage stateRecord = genericDao.selectState(
		seed.getEntityId(), seed.getRecordId());

	if (stateRecord != null) {

	    handleRecordExists(stateRecord, seed, recordData, entityName);

	} else {
	    handleRecordDoesNotExist(stateRecord, seed, recordData, entityName);
	}

    }

    private void handleRecordDoesNotExist(SyncEntityMessage stateRecord,
	    SeedRecord seed, SyncEntityMessage recordData, String entityName)
	    throws Exception {
	// If the record DOES NOT exist in the SyncState table:
	// 1. insert the User table with the new value
	genericDao.save(entityName, recordData);

	// 2. insert the SyncState table with the new value
	SyncEntityMessage syncState = new DefaultSyncEntityMessage();
	syncState.setEntity(entityGetter.getSyncStateName());
	syncState.set("ENTITYID", entityGetter.getId(entityName));
	syncState.set("RECORDID", recordData.getCalculatedPrimaryKey());
	// String recordJson = jsonMapper.writeValueAsString(recordData);
	String recordString = stringify.toString(recordData);
	syncState.set("RECORDDATA", recordString);
	// syncState.set("RECORDHASH", recordData.generateHash());
	syncState.set("RECORDHASH", seed.getRecordHash());

	// LOG.info("Inserting the SyncState table with the new value [{}]",
	// syncState);
	genericDao.save(entityGetter.getSyncStateName(), syncState);

	LOG.info("Record does not exist in SyncTable, so inserted User "
		+ "and State tables with data for "
		+ "entityId={}, recordId={}", seed.getEntityId(),
		seed.getRecordId());
    }

    private void handleRecordExists(SyncEntityMessage stateRecord,
	    SeedRecord seed, SyncEntityMessage recordData, String entityName)
	    throws Exception {
	// If the record exists in the SyncState table, check if the
	// hashes match.
	LOG.debug("Record exists in the SyncState table {}", stateRecord);
	if (seed.getRecordHash().equals(stateRecord.get("RECORDHASH"))) {
	    // If the record exists in the SyncState table and the
	    // hashes match, break and go to next message
	    LOG.info("Record exists and hashes match, so "
		    + "did nothing for entityId={}, recordId={}",
		    seed.getEntityId(), seed.getRecordId());
	    return;
	} else {
	    handleRecordDoesNotMatch(stateRecord, seed, recordData, entityName);
	}
    }

    private void handleRecordDoesNotMatch(SyncEntityMessage stateRecord,
	    SeedRecord seed, SyncEntityMessage recordData, String entityName)
	    throws Exception {
	// If the record exists in the SyncState table and the hash
	// does not match:

	SyncEntityMessage myJSON = jsonMapper.readValue(
		String.valueOf(stateRecord.get("RECORDDATA")),
		SyncEntityMessage.class);
	// 1. run the standard merge logic (a MergeStrategy class)
	// using the existing record in the User table and the newly
	// received message (there are some error conditions here
	// for advanced conflicts)
	LOG.debug("Hash does not match, run the standard merge logic");
	SyncEntityMessage resolvedRecordData = conflictResolver.resolve(myJSON,
		recordData);

	if (resolvedRecordData == null) {
	    // TODO Should there be different error handling here?
	    LOG.warn("Record exists and hashes do not match, but Conflict "
		    + "resolver could not determine a syncEntityMessage "
		    + "for entityId={}, recordId={}", seed.getEntityId(),
		    seed.getRecordId());
	    return;
	} else {
	    writeChangedRecord(resolvedRecordData, seed, recordData, entityName);
	}
    }

    private void writeChangedRecord(SyncEntityMessage resolvedRecordData,
	    SeedRecord seed, SyncEntityMessage recordData, String entityName)
	    throws Exception {
	// 2. update the User table with the new value
	genericDao.saveOrUpdate(entityName, resolvedRecordData,
		recordIdGetter.get(entityName));
	updateSyncStateTable(resolvedRecordData, entityName);
	LOG.info("Record exists and hashes do not match, so updated User "
		+ "and State tables with merged data for "
		+ "entityId={}, recordId={}", seed.getEntityId(),
		seed.getRecordId());
    }

    private void updateSyncStateTable(SyncEntityMessage resolvedRecordData,
	    String entityName) throws Exception {
	// 3. update the SyncState table with the newly
	// calculated hash
	SyncEntityMessage syncState = new DefaultSyncEntityMessage();
	syncState.setEntity(entityGetter.getSyncStateName());
	syncState.set("ENTITYID", entityGetter.getId(entityName));
	syncState.set("RECORDID",
		resolvedRecordData.get(recordIdGetter.get(entityName)));
	String recordString = stringify.toString(resolvedRecordData);
	syncState.set("RECORDDATA", recordString);
	String newHash = hasher.generate(recordString);
	syncState.set("RECORDHASH", newHash);
	genericDao.update(entityGetter.getSyncStateName(), syncState,
		recordIdGetter.get(entityGetter.getSyncStateName()));
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
	answer.append("genericDao=");
	answer.append(genericDao);
	answer.append("}");
	return (answer.toString());
    }

}
