/**
 * 
 */
package tools.datasync.basic.sync.pump;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import tools.datasync.basic.comm.SyncMessage;
import tools.datasync.basic.comm.SyncMessageType;
import tools.datasync.basic.model.SeedRecord;
import tools.datasync.basic.seed.SeedException;
import tools.datasync.basic.seed.SeedOverException;
import tools.datasync.basic.seed.SeedProducer;
import tools.datasync.basic.util.HashGenerator;
import tools.datasync.basic.util.JSONMapperBean;
import tools.datasync.basic.util.Md5HashGenerator;
import tools.datasync.basic.util.NLogger;

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
 * @since   29-Nov-2014
 */
public class JvmSyncPumpSender implements Runnable {

    BlockingQueue<String> sendQueue;
    AtomicBoolean isRunning;
    
    SeedProducer seedProducer = null;
    JSONMapperBean jsonMapper = null;
    HashGenerator hashGen = null;
    int messageNumber = 0;
    
    Logger logger = Logger.getLogger(JvmSyncPumpSender.class.getName());
    NLogger nlogger = NLogger.getLogger();
    
    public JvmSyncPumpSender(BlockingQueue<String> sendQueue){
    
        this.sendQueue = sendQueue;
        this.isRunning = new AtomicBoolean(true);
        this.jsonMapper = JSONMapperBean.getInstance();
        this.hashGen = Md5HashGenerator.getInstance();
    }
    
    public void setSeedProducer(SeedProducer seedProducer) {
        this.seedProducer = seedProducer;
    }
    
    @Override
    public void run() {
        isRunning.set(true);
        
        logger.info("Started JvmSyncPumpSender...");
        while(! Thread.currentThread().isInterrupted()){
            try {
                SeedRecord seed = seedProducer.getNextSeed();
                String payloadJson = jsonMapper.writeValueAsString(seed);
                String paloadHash = hashGen.generate(payloadJson);
                SyncMessage syncMessage = new SyncMessage(seed.getOrigin(), messageNumber++, 
                        SyncMessageType.SEED.toString(), payloadJson, paloadHash, System.currentTimeMillis());
                String message = jsonMapper.writeValueAsString(syncMessage);
                
            } catch (SeedOverException soe) {
                nlogger.log(soe, Level.INFO, "Seed phase is over... Terminating the sender process logic.");
                break;
            } catch (SeedException se) {
                nlogger.log(se, Level.WARNING, "Error while creating seed record.");
            } catch (JsonGenerationException | JsonMappingException  jme) {
                nlogger.log(jme, Level.WARNING, "Error while creating JSON.");
            } catch (IOException ioe) {
                nlogger.log(ioe, Level.WARNING, "Error while creating JSON.");
            }
        }
        logger.info("put 1");
        sendQueue.offer("Data 1");
        logger.info("put 2");
        sendQueue.offer("Data 2");
        logger.info("put 3");
        sendQueue.offer("Data 3");
        logger.info("put 4");
        sendQueue.offer("Data 4");
        
        isRunning.set(false);
    }

    public AtomicBoolean isRunning(){
        return isRunning;
    }
}
