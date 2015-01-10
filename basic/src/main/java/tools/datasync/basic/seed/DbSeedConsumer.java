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

import org.apache.log4j.Logger;

import tools.datasync.basic.dao.GenericDao;
import tools.datasync.basic.dao.SyncDao;
import tools.datasync.basic.logic.ConflictException;
import tools.datasync.basic.logic.ConflictResolver;
import tools.datasync.basic.logic.InitiatorWinsConflictResolver;
import tools.datasync.basic.model.Ids;
import tools.datasync.basic.model.JSON;
import tools.datasync.basic.model.SeedRecord;
import tools.datasync.basic.util.JSONMapperBean;
import tools.datasync.basic.util.Md5HashGenerator;

public class DbSeedConsumer implements SeedConsumer {

	SyncDao syncDao;
	Logger logger = Logger.getLogger(DbSeedConsumer.class.getName());
    private JSONMapperBean jsonMapper;
    private Md5HashGenerator hashGenerator;
    private GenericDao genericDao;
    private ConflictResolver conflictResolver;

    public DbSeedConsumer(ConflictResolver conflictResolver) {
        this.jsonMapper = JSONMapperBean.getInstance();
        this.hashGenerator = Md5HashGenerator.getInstance();
        this.conflictResolver = conflictResolver;
    }
    
    public void setGenericDao(GenericDao genericDao){
        this.genericDao = genericDao;
    }
    
	public boolean consume(SeedRecord seed) throws IOException, SeedException {
		
	    logger.debug("Consuming SEED Record: " + seed);
	    
        String entityName = Ids.EntityId.getTableName(seed.getEntityId());
        String dbRecord = seed.getRecordJson();
        JSON json = jsonMapper.readValue(dbRecord, JSON.class);
        
        if(! hashGenerator.validate(dbRecord, seed.getRecordHash())){
            //throw new SeedException("Illegal message - Record does not match with its hash");
            logger.warn("Illegal message - Record does not match with its hash");
        }
        
        
        
        
        //If the record exists in the SyncState table, check if the hashes match.
        
        //If the record exists in the SyncState table and the hashes match, break and go to next message
        
        //If the record exists in the SyncState table and the hash does not match:
        //1. run the standard merge logic (a MergeStrategy class) using the existing record in the User table and the newly received message (there are some error conditions here for advanced conflicts)
        //2. update the User table with the new value
        //3. update the SyncState table with the newly calculated hash
        
        //If the record DOES NOT exist in the SyncState table:
        //1. insert the User table with the new value
        //2. insert the SyncState table with the new value
        
        try {
        	JSON stateRecord = genericDao.selectState(seed.getEntityId(), seed.getRecordId());
        	
        	if(stateRecord != null){
        		//If the record exists in the SyncState table, check if the hashes match.
        		logger.info(stateRecord);
        		if(seed.getRecordHash().equals(stateRecord.get("RECORDHASH"))){
        			//If the record exists in the SyncState table and the hashes match, break and go to next message
        			return true;
        		} else {
        			//If the record exists in the SyncState table and the hash does not match:
        			
        			JSON myJSON = jsonMapper.readValue(String.valueOf(stateRecord.get("RECORDDATA")),
        					JSON.class);
        			//1. run the standard merge logic (a MergeStrategy class) using the existing record in the User table and the newly received message (there are some error conditions here for advanced conflicts)
        			JSON resolvedJSON = conflictResolver.resolve(myJSON, json);
        			
        			if(resolvedJSON == null){
        				return true;
        			}
        			else{
        				//2. update the User table with the new value
            	        //3. update the SyncState table with the newly calculated hash
        				genericDao.save(entityName, resolvedJSON);
        			}
        		}
        		
        	} else {
        		genericDao.save(entityName, json);
        	}
            
        } catch (SQLException | ConflictException e) {
            throw new IOException(e.getMessage(), e);
        }

	    return true;
	}

}
