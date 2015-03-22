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
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.basic.dao.GenericDao;
import tools.datasync.basic.model.EntityGetter;
import tools.datasync.basic.model.SeedRecord;
import tools.datasync.basic.model.SyncEntityMessage;
import tools.datasync.basic.util.ObjectMapperFactory;

public class DbSeedProducer implements SeedProducer {

    private static final Logger LOG = LoggerFactory
	    .getLogger(DbSeedProducer.class);

    private EntityGetter entityGetter;
    private GenericDao genericDao;
    private ObjectMapper jsonMapper = ObjectMapperFactory.getInstance();
    private boolean isRunning = false;

    boolean stop = false;

    private final List<String> tables;

    private Iterator<SyncEntityMessage> currentJsonIterator = null;
    private Iterator<String> tableNameIterator = new Iterator<String>() {
	int index = 0;

	public String next() {
	    if (index < tables.size()) {
		return tables.get(index++);
	    }
	    return null;
	}

	public boolean hasNext() {
	    return (index < tables.size());
	}

	public void remove() {
	    // do nothing...
	};
    };

    public DbSeedProducer(GenericDao genericDao, List<String> tables,
	    EntityGetter entityGetter) {

	this.genericDao = genericDao;
	this.tables = tables;
	this.entityGetter = entityGetter;
	this.isRunning = true;
    }

    public GenericDao getGenericDao() {
	return this.genericDao;
    }

    private boolean hasNextSeed() throws SeedException {
	// if this is the first time through, we ignore
	if (currentJsonIterator != null) {
	    if (currentJsonIterator.hasNext()) {
		return true;
	    }
	}

	return (processTable());
    }

    private boolean processTable() throws SeedException {
	while (this.tableNameIterator.hasNext()) {
	    String tableName = this.tableNameIterator.next();
	    try {
		this.currentJsonIterator = genericDao
			.selectAll(tableName, true);
	    } catch (SQLException e) {
		throw new SeedException(e);
	    }
	    // Invoke for the first time...
	    if (currentJsonIterator.hasNext()) {
		return true;
	    } else {
		LOG.info("No records in table [" + tableName
			+ "], moving to next table");
	    }
	}
	LOG.info("No more tables to process");
	return false;

    }

    public SeedRecord getNextSeed() throws SeedOverException, SeedException {

	if (hasNextSeed()) {
	    SyncEntityMessage json = this.currentJsonIterator.next();
	    SeedRecord seed = this.createSeed(json);
	    return seed;

	    // Fix: This statement is causing early finish.
	    // isRunning = tableNameIterator.hasNext();
	} else {

	    this.isRunning = false;
	    return null;
	    // TODO: Should we throw here ?
	    // throw (new RuntimeException(e));
	    // throw new SeedOverException("No more tables to seed from.");
	}

    }

    public boolean isRunning() {
	return this.isRunning;
    }

    private SeedRecord createSeed(SyncEntityMessage record)
	    throws SeedException {

	SeedRecord seed = null;
	try {

	    seed = createSeedRecord(record);

	} catch (IOException e) {
	    LOG.warn("Error while JSON Serialization", e);
	    throw new SeedException("Error while JSON Serialization", e);
	}

	return seed;
    }

    private SeedRecord createSeedRecord(SyncEntityMessage record)
	    throws JsonGenerationException, JsonMappingException, IOException {
	String entityId = entityGetter.getId(record.getEntity());
	String recordId = String.valueOf(record.getCalculatedPrimaryKey());
	String recordJson = jsonMapper.writeValueAsString(record);
	String recordHash = record.generateHash();
	String origin = "";// me.getPeerId();

	SeedRecord seed = new SeedRecord();
	seed.setEntityId(entityId);
	seed.setRecordId(recordId);
	seed.setRecordHash(recordHash);
	seed.setRecordJson(recordJson);
	seed.setOrigin(origin);

	LOG.debug("generated seed record: " + seed);
	return seed;
    }
}
