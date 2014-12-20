/**
 * 
 */
package tools.datasync.basic.sync.pump;

import java.util.concurrent.BlockingQueue;

import tools.datasync.basic.sync.SyncPeer;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @author Upendra Jariya
 * @sponsor Douglas Johnson
 * @version 1.0
 * @since 29-Nov-2014
 */
public class JvmSyncPump implements SyncPump {

    // threading
    // fetching data
    // constructing messages
    // send messages
    // indication of completeness
    
    SyncPeer syncPeerMe = null;
    SyncPeer syncPeerOther = null;
    
    JvmSyncPumpSender sender = null;
    JvmSyncPumpReceiver receiver = null;
    
    boolean isPumping = false;
    
    /**
     * @param syncPeerMe
     * @param syncPeerOther
     */
    public JvmSyncPump(SyncPeer syncPeerMe, SyncPeer syncPeerOther,
            JvmSyncPumpSender sender, JvmSyncPumpReceiver receiver) {
        super();
        this.syncPeerMe = syncPeerMe;
        this.syncPeerOther = syncPeerOther;
        
        this.sender = sender;
        this.receiver = receiver;
        this.isPumping = false;
    }

    @Override
    public void beginPump() {
        
        Thread senderThread = new Thread(sender);
        Thread receiverThread = new Thread(receiver);
        
        senderThread.start();
        receiverThread.start();
        
        this.isPumping = true;
        
        BlockingQueue<String> receiveQueue = receiver.getQueue();
        
        while(sender.isRunning().get()
                && receiver.isRunning().get()){
            try {
                Thread.sleep(100);
                
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        this.isPumping = false;
        //throw (new RuntimeException("Not implemented"));
    }

    @Override
    public boolean isPumping() {
        return this.isPumping;
    }

}
