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

import org.apache.log4j.Logger;

import tools.datasync.basic.dao.GenericDao;
import tools.datasync.basic.model.Ids;
import tools.datasync.basic.model.JSON;
import tools.datasync.basic.model.SeedRecord;
import tools.datasync.basic.util.JSONMapperBean;

public class DbSeedProducer implements SeedProducer {

    private GenericDao genericDao;
    // private HashGenerator hashGenerator;
    private JSONMapperBean jsonMapper;
    private boolean isRunning = false;

    private static Logger LOG = Logger.getLogger(DbSeedProducer.class);
    boolean stop = false;

    private final List<String> tables;

    private Iterator<JSON> currentJsonIterator = null;
    private Iterator<String> tableNameIterator = new Iterator<String>() {
	// String[] tables = { Ids.Table.CONTACT, Ids.Table.WORK_HISTORY,
	// Ids.Table.CONTACT_LINK };
	int index = 0;

	// @Override
	public String next() {
	    if (index < tables.size()) {
		return tables.get(index++);
	    }
	    return null;
	}

	// @Override
	public boolean hasNext() {
	    return (index < tables.size());
	}

	public void remove() {
	    // do nothing...
	};
    };

    public DbSeedProducer(GenericDao genericDao, List<String> tables) {

	this.genericDao = genericDao;
	this.tables = tables;
	this.jsonMapper = JSONMapperBean.getInstance();
	// this.hashGenerator = Md5HashGenerator.getInstance();
	this.isRunning = true;
    }

    // @Override
    // public void setGenericDao(GenericDao genericDao) {
    // this.genericDao = genericDao;
    // }

    // @Override
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

    // @Override
    public SeedRecord getNextSeed() throws SeedOverException, SeedException {

	// if (this.currentJsonIterator == null
	// || !this.currentJsonIterator.hasNext()) {

	if (hasNextSeed()) {
	    JSON json = this.currentJsonIterator.next();
	    SeedRecord seed = this.createSeed(json);
	    return seed;

	    // Fix: This statement is causing early finish.
	    // isRunning = tableNameIterator.hasNext();
	} else {

	    this.isRunning = false;
	    LOG.info("No more tables to seed from");
	    return null;
	    // TODO: Should we throw here ?
	    // throw (new RuntimeException(e));
	    // throw new SeedOverException("No more tables to seed from.");
	}
	// }

    }

    // @Override
    public boolean isRunning() {
	return this.isRunning;
    }

    private SeedRecord createSeed(JSON record) throws SeedException {

	SeedRecord seed = null;
	try {
	    String entityId = Ids.EntityId.get(record.getEntity());
	    String recordId = String.valueOf(record.getCalculatedPrimaryKey());
	    String recordJson = jsonMapper.writeValueAsString(record);
	    String recordHash = record.generateHash();
	    // TODO: get peer id from database
	    String origin = "";// me.getPeerId();
	    seed = new SeedRecord(entityId, recordId, recordHash, recordJson,
		    origin);
	    LOG.debug("generated seed record: " + seed);

	} catch (IOException e) {
	    LOG.warn("Error while JSON Serialization", e);
	    throw new SeedException("Error while JSON Serialization", e);
	}

	return seed;
    }
}
