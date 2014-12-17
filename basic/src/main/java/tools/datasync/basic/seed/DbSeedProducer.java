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
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import tools.datasync.basic.dao.GenericDao;
import tools.datasync.basic.model.Ids;
import tools.datasync.basic.model.JSON;
import tools.datasync.basic.model.SeedRecord;
import tools.datasync.basic.util.HashGenerator;
import tools.datasync.basic.util.JSONMapperBean;
import tools.datasync.basic.util.Md5HashGenerator;
import tools.datasync.basic.util.NLogger;

public class DbSeedProducer implements SeedProducer {

    NLogger nlogger = NLogger.getLogger();
    GenericDao genericDao;
    HashGenerator hashGenerator;
    JSONMapperBean jsonMapper;
    boolean isRunning = false;

    Logger logger = Logger.getLogger(DbSeedProducer.class.getName());
    boolean stop = false;
    
    Iterator<JSON> currentJsonIterator = null;
    Iterator<String> tableNameIterator = new Iterator<String>() {
        String[] tables = { Ids.Table.CONTACT, Ids.Table.WORK_HISTORY, Ids.Table.CONTACT_LINK };
        int index = 0;

        @Override
        public String next() {
            if (index < tables.length) {
                return tables[index++];
            }
            return null;
        }

        @Override
        public boolean hasNext() {
            return (index < tables.length);
        }
    };

    public DbSeedProducer() {
        
        this.jsonMapper = JSONMapperBean.getInstance();
        this.hashGenerator = Md5HashGenerator.getInstance();
        this.isRunning = true;
    }
    
    public void setGenericDao(GenericDao genericDao){
        this.genericDao = genericDao;
    }

    @Override
    public SeedRecord getNextSeed() throws SeedOverException, SeedException {

        if (this.currentJsonIterator == null || !this.currentJsonIterator.hasNext()) {

            if (this.tableNameIterator.hasNext()) {

                String tableName = this.tableNameIterator.next();
                this.currentJsonIterator = genericDao.selectAll(tableName);
            } else {

                this.isRunning = false;
                throw new SeedOverException("No more tables to seed from.");
            }
        }

        JSON json = this.currentJsonIterator.next();
        SeedRecord seed = this.createSeed(json);
        return seed;
    }

    @Override
    public boolean isRunning() {
        return this.isRunning;
    }

    private SeedRecord createSeed(JSON record) throws SeedException {

        SeedRecord seed = null;
        try {
            String entityId = Ids.EntityId.get(record.getEntity());
            String recordId = String.valueOf(record.get(Ids.KeyColumn.get(record.getEntity())));
            String recordJson = jsonMapper.writeValueAsString(record);
            String recordHash = hashGenerator.generate(recordJson);
            // TODO: get peer id from database
            String origin = "";//me.getPeerId();
            seed = new SeedRecord(entityId, recordId, recordHash, recordJson, origin);
            logger.finest("generated seed record: " + seed);

        } catch (IOException e) {
            nlogger.log(e, Level.WARNING, "Error while JSON Serialization", record);
            throw new SeedException("Error while JSON Serialization", e);
        }

        return seed;
    }
}
