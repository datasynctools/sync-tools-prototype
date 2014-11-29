package tools.datasync.basic.seed;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Before;
import org.junit.Test;

import tools.datasync.basic.sync.SyncManager;
import tools.datasync.basic.sync.SyncOrchestrationManager;
import tools.datasync.basic.sync.SyncPeer;
import tools.datasync.basic.sync.SyncSession;
import tools.datasync.basic.sync.pump.JvmSyncPumpFactory;
import tools.datasync.basic.sync.pump.SyncPumpFactory;

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
public class SeedIntegrationTest {

    SyncOrchestrationManager syncOrchMgr;
    
    SyncManager peerA = null;
    SyncManager peerB = null;
    
    @Before
    public void init(){
        
        SyncPeer syncPeerA = new SyncPeer("A");
        SyncPeer syncPeerB = new SyncPeer("B");
        
        BlockingQueue<String> a2bQueue = new LinkedBlockingQueue<String>();
        BlockingQueue<String> b2aQueue = new LinkedBlockingQueue<String>();
        
        SyncPumpFactory pumpFactoryA = new JvmSyncPumpFactory(syncPeerA, syncPeerB, a2bQueue, b2aQueue);
        SyncPumpFactory pumpFactoryB = new JvmSyncPumpFactory(syncPeerB, syncPeerA, b2aQueue, a2bQueue);
        syncOrchMgr = new SyncOrchestrationManager(pumpFactoryA, pumpFactoryB);
        
    }
    
    @Test
    public void firstTest(){
        
        SyncSession syncSession = syncOrchMgr.createSession();
        
        syncSession.doSync();
        
        // TODO: Verify the state of both databases.
        
    }

//  SyncConnection connectionA = null;
//  SyncConnection connectionB = null;
//  
//  connectionA = new JvmSyncConnection(peerA);
//  connectionB = new JvmSyncConnection(peerB);
//  
//  connectionA.setOtherConnection(connectionB);
//  connectionB.setOtherConnection(connectionA);
//  
//  peerA = new SyncManagerImpl("A", connectionA);
//  peerB = new SyncManagerImpl("B", connectionB);

    
    
//    peerA.initiate();
//  peerB.initiate();
//  
//  SyncMessage beginsync = new SyncMessage("", 1, SyncMessageType.BEGIN_SYNC.toString(), null, null, System.currentTimeMillis());
//  SyncMessage beginseed = new SyncMessage("", 1, SyncMessageType.BEGIN_SEED.toString(), null, null, System.currentTimeMillis());
//  
//  peerA.send(beginsync);
//  peerB.send(beginsync);
//  
//  peerA.send(beginseed);
//  peerB.send(beginseed);
}
