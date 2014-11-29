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
package tools.datasync.basic.sync;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import tools.datasync.basic.comm.SyncConnection;
import tools.datasync.basic.comm.SyncMessage;
import tools.datasync.basic.comm.SyncMessageType;
import static tools.datasync.basic.comm.SyncMessageType.*;
import tools.datasync.basic.model.SeedRecord;
import tools.datasync.basic.seed.DbSeedConsumer;
import tools.datasync.basic.seed.DbSeedProducer;
import tools.datasync.basic.seed.SeedConsumer;
import tools.datasync.basic.seed.SeedProducer;
import tools.datasync.basic.sync.fsm.SyncStateElement;
import static tools.datasync.basic.sync.fsm.SyncStateElement.*;
import tools.datasync.basic.util.HashGenerator;
import tools.datasync.basic.util.JSONMapperBean;
import tools.datasync.basic.util.Md5HashGenerator;
import tools.datasync.basic.util.NLogger;

public class SyncManagerImpl implements SyncManager {

    private NLogger nlogger;

    private final ReentrantLock syncLock = new ReentrantLock();
    private SyncPeer me = null;
    private SyncPeer currentPeer = null;
    private SyncConnection connection;
    private int messageNumber = 0;
    private JSONMapperBean mapperBean = null;
    private HashGenerator hashGen = null;
    private SeedConsumer consumer = null;
    private SyncStateElement state = INVALID;
    
    private Thread producerThread;

    public SyncManagerImpl(String myName) {
        super();
        this.connection = connection;
        this.initiate();
        this.me = new SyncPeer(myName);
    }
    
    public void setConnection(SyncConnection connection){
        this.connection = connection;
    }

    public void initiate() {
        try {
            mapperBean = JSONMapperBean.getInstance();
            this.hashGen = new Md5HashGenerator();
            this.consumer = new DbSeedConsumer();
            this.state = READY;
            
        } catch (Exception ex) {
            nlogger.log(ex, Level.SEVERE, "Initialization Failure");
        }
    }

    public boolean beginSync(SyncPeer peer) {

        if (syncLock.tryLock()) {
            this.currentPeer = peer;
            return true;
        } else {
            return false;
        }
    }

    public boolean endSync(SyncPeer peer) {

        syncLock.unlock();
        this.currentPeer = null;
        return true;
    }

    public void seedIn(SeedRecord seed) {

        consumer.consume(seed);
    }

    public void seedOut(SeedRecord seed) {

        try {
            String seedStr = mapperBean.writeValueAsString(seed);
            SyncMessage message = new SyncMessage();
            message.setMessageNumber(messageNumber++);
            message.setMessageType(SyncMessageType.SEED.toString());
            message.setOriginId(me.getPeerId());
            message.setPayloadJson(seedStr);
            message.setPaloadHash(hashGen.generate(seedStr));

            this.send(message);
        } catch (IOException ex) {
            nlogger.log(ex, Level.SEVERE, "Exception while sending seed record...");
        }

    }

    public void send(SyncMessage message) {
        connection.send(message);
    }

    public void onData(SyncMessage message) {

        SyncMessageType event = SyncMessageType.get(message.getMessageType());
        if (this.state.equals(READY)) {
            if (BEGIN_SYNC.equals(event)) {
                // ignore
            } else if (BEGIN_SEED.equals(event)) {
                if(this.state.hasTransition(event)){
                    this.state = this.state.getTransition(event);
                    
                    if(SEEDING.equals(this.state)){
                        // Start the producer thread
                        SeedProducer producer = new DbSeedProducer(this, currentPeer);
                        producerThread = new Thread(producer);
                        producerThread.start();
                    }
                }
            }
        }
        else if (this.state.equals(SEEDING)) {
            if (SEED.equals(event)) {
                String seedStr = message.getPayloadJson();
                try {
                    SeedRecord seed = mapperBean.readValue(seedStr, SeedRecord.class);
                    consumer.consume(seed);
                } catch (IOException e) {
                    nlogger.log(e, Level.WARNING, "Unable to parse seed record.");
                }
            }
        }
        else if (this.state.equals(SEED_OVER_INIT)) {
            
        }
        else if (this.state.equals(SEED_OVER_FOLLOW)) {
            
        }
        else if (this.state.equals(SYNC_OVER)) {
            
        }
    }
}
