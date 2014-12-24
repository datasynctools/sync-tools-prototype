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
import java.util.logging.Logger;

import tools.datasync.basic.dao.GenericDao;
import tools.datasync.basic.dao.SyncDao;
import tools.datasync.basic.model.Ids;
import tools.datasync.basic.model.JSON;
import tools.datasync.basic.model.SeedRecord;
import tools.datasync.basic.util.JSONMapperBean;
import tools.datasync.basic.util.Md5HashGenerator;
import tools.datasync.basic.util.NLogger;

public class DbSeedConsumer implements SeedConsumer {

	SyncDao syncDao;
	Logger logger = NLogger.getLogger(DbSeedConsumer.class.getName());
    private JSONMapperBean jsonMapper;
    private Md5HashGenerator hashGenerator;
    private GenericDao genericDao;

    public DbSeedConsumer() {
        this.jsonMapper = JSONMapperBean.getInstance();
        this.hashGenerator = Md5HashGenerator.getInstance();
    }
    
    public void setGenericDao(GenericDao genericDao){
        this.genericDao = genericDao;
    }
    
	public boolean consume(SeedRecord seed) throws IOException, SeedException {
		
	    logger.fine("Consuming SEED Record: " + seed);
	    
        String entityName = Ids.EntityId.getTableName(seed.getEntityId());
        String dbRecord = seed.getRecordJson();
        JSON json = jsonMapper.readValue(dbRecord, JSON.class);
        
        if(! hashGenerator.validate(dbRecord, seed.getRecordHash())){
            //throw new SeedException("Illegal message - Record does not match with its hash");
            logger.warning("Illegal message - Record does not match with its hash");
        }
        
        try {
            genericDao.save(entityName, json);
        } catch (SQLException e) {
            throw new IOException(e.getMessage(), e);
        }

	    return true;
	}

}
