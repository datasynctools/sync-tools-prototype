/**
 * 
 */
package tools.datasync.basic.sync.pump;

import java.util.concurrent.BlockingQueue;

import tools.datasync.basic.sync.SyncPeer;

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
public class JvmSyncPumpFactory implements SyncPumpFactory {

    SyncPeer syncPeerMe = null;
    SyncPeer syncPeerOther = null;
    BlockingQueue<String> sendQueue = null;
    BlockingQueue<String> receiveQueue = null;
    
    /**
     * @param syncPeerMe
     * @param syncPeerOther
     */
    public JvmSyncPumpFactory(SyncPeer syncPeerMe, SyncPeer syncPeerOther,
            BlockingQueue<String> sendQueue, BlockingQueue<String> receiveQueue) {
        super();
        this.syncPeerMe = syncPeerMe;
        this.syncPeerOther = syncPeerOther;
        this.sendQueue = sendQueue;
        this.receiveQueue = receiveQueue;
    }

    public SyncPump getInstance(){
        
        JvmSyncPumpSender sender = new JvmSyncPumpSender(sendQueue);
        JvmSyncPumpReceiver receiver = new JvmSyncPumpReceiver(receiveQueue);
        
        return new JvmSyncPump(syncPeerMe, syncPeerOther, sender, receiver);
    }
}
