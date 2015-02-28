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

import org.apache.log4j.Logger;

import tools.datasync.basic.dao.GenericDao;
import tools.datasync.basic.logic.ConflictResolver;
import tools.datasync.basic.model.IdGetter;
import tools.datasync.basic.model.Ids;
import tools.datasync.basic.model.JSON;
import tools.datasync.basic.model.SeedRecord;
import tools.datasync.basic.util.JSONMapperBean;
import tools.datasync.basic.util.Md5HashGenerator;

public class DbSeedConsumer implements SeedConsumer {

    private static final Logger LOG = Logger.getLogger(DbSeedConsumer.class);

    private JSONMapperBean jsonMapper = JSONMapperBean.getInstance();
    private Md5HashGenerator hashGenerator = Md5HashGenerator.getInstance();
    private GenericDao genericDao;
    private ConflictResolver conflictResolver;
    private IdGetter idGetter;

    public DbSeedConsumer(ConflictResolver conflictResolver, IdGetter idGetter,
	    GenericDao genericDao) {
	this.conflictResolver = conflictResolver;
	this.idGetter = idGetter;
	this.genericDao = genericDao;
    }

    private void validate(SeedRecord seed, String dbRecord) {
	if (!hashGenerator.validate(dbRecord, seed.getRecordHash())) {
	    // throw new
	    // SeedException("Illegal message - Record does not match with its hash");

	    // TODO Is this the right logic?
	    LOG.warn("Illegal message - Record does not match with its hash\n"
		    + dbRecord);
	}
    }

    public void consume(SeedRecord seed) throws IOException, SeedException {

	LOG.debug("Consuming SEED Record: " + seed);

	String entityName = Ids.EntityId.getTableName(seed.getEntityId());
	String dbRecord = seed.getRecordJson();
	JSON json = jsonMapper.readValue(dbRecord, JSON.class);

	validate(seed, dbRecord);

	try {

	    handle(seed, json, entityName);

	} catch (Exception e) {
	    LOG.error("Error while consuming record: ", e);
	    throw new IOException(e.getMessage(), e);
	}

    }

    private void handle(SeedRecord seed, JSON json, String entityName)
	    throws Exception {
	JSON stateRecord = genericDao.selectState(seed.getEntityId(),
		seed.getRecordId());

	if (stateRecord != null) {

	    handleRecordExists(stateRecord, seed, json, entityName);

	} else {
	    handleRecordDoesNotExist(stateRecord, seed, json, entityName);
	}

    }

    private void handleRecordDoesNotExist(JSON stateRecord, SeedRecord seed,
	    JSON json, String entityName) throws Exception {
	// If the record DOES NOT exist in the SyncState table:
	// 1. insert the User table with the new value
	LOG.info("Record DOES NOT exist in the SyncState table, inserting the User table with the new value");
	genericDao.save(entityName, json);

	// 2. insert the SyncState table with the new value
	JSON syncState = new JSON(Ids.Table.SYNC_STATE);
	syncState.set("ENTITYID", Ids.EntityId.get(entityName));
	syncState.set("RECORDID", json.getCalculatedPrimaryKey());
	String recordJson = jsonMapper.writeValueAsString(json);
	syncState.set("RECORDDATA", recordJson);
	syncState.set("RECORDHASH", json.generateHash());

	LOG.info("inserting the SyncState table with the new value");
	genericDao.save(Ids.Table.SYNC_STATE, syncState);
    }

    private void handleRecordExists(JSON stateRecord, SeedRecord seed,
	    JSON json, String entityName) throws Exception {
	// If the record exists in the SyncState table, check if the
	// hashes match.
	LOG.info("Record exists in the SyncState table" + stateRecord);
	if (seed.getRecordHash().equals(stateRecord.get("RECORDHASH"))) {
	    // If the record exists in the SyncState table and the
	    // hashes match, break and go to next message
	    LOG.debug("Hashes match, break and go to next message");
	    return;
	} else {
	    handleRecordDoesNotMatch(stateRecord, seed, json, entityName);
	}
    }

    private void handleRecordDoesNotMatch(JSON stateRecord, SeedRecord seed,
	    JSON json, String entityName) throws Exception {
	// If the record exists in the SyncState table and the hash
	// does not match:

	JSON myJSON = jsonMapper.readValue(
		String.valueOf(stateRecord.get("RECORDDATA")), JSON.class);
	// 1. run the standard merge logic (a MergeStrategy class)
	// using the existing record in the User table and the newly
	// received message (there are some error conditions here
	// for advanced conflicts)
	LOG.info("Hash does not match, run the standard merge logic");
	JSON resolvedJSON = conflictResolver.resolve(myJSON, json);

	if (resolvedJSON == null) {
	    LOG.debug("Conflict resolver did not have a record to write");
	    return;
	} else {
	    writeChangedRecord(resolvedJSON, seed, json, entityName);
	}
    }

    private void writeChangedRecord(JSON resolvedJSON, SeedRecord seed,
	    JSON json, String entityName) throws Exception {
	// 2. update the User table with the new value
	LOG.info("Update the User table with the new value");
	genericDao.saveOrUpdate(entityName, resolvedJSON,
		idGetter.get(entityName));

	// 3. update the SyncState table with the newly
	// calculated hash
	JSON syncState = new JSON(Ids.Table.SYNC_STATE);
	syncState.set("ENTITYID", Ids.EntityId.get(entityName));
	syncState.set("RECORDID", resolvedJSON.get(idGetter.get(entityName)));
	String recordJson = jsonMapper.writeValueAsString(resolvedJSON);
	syncState.set("RECORDDATA", recordJson);
	syncState.set("RECORDHASH", resolvedJSON.generateHash());

	LOG.info("Update the SyncState table with the newly calculated hash.");
	genericDao.update(Ids.Table.SYNC_STATE, syncState,
		idGetter.get(Ids.Table.SYNC_STATE));
    }

}
