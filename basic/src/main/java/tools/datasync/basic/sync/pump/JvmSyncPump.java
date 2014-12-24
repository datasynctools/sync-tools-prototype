/**
 * 
 */
package tools.datasync.basic.sync.pump;

import java.util.logging.Logger;

import tools.datasync.basic.util.NLogger;

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
    
    PeerMode peerMode;
    JvmSyncPumpSender sender = null;
    JvmSyncPumpReceiver receiver = null;
    Logger logger = NLogger.getLogger(JvmSyncPump.class.getName());
    
    boolean isPumping = false;
    
    public JvmSyncPump(PeerMode peerMode,
            JvmSyncPumpSender sender, JvmSyncPumpReceiver receiver) {
        super();
        
        this.peerMode = peerMode;
        this.sender = sender;
        this.receiver = receiver;
        this.isPumping = false;
    }

    @Override
    public void beginPump() {
        
        Thread senderThread = new Thread(sender, "Sender-"+this.peerMode.name());
        Thread receiverThread = new Thread(receiver, "Receiver-"+this.peerMode.name());
        
        senderThread.start();
        receiverThread.start();
        
        this.isPumping = true;
        
        while(sender.isRunning().get()
                || receiver.isRunning().get()){
            try {
                Thread.sleep(100);
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        this.isPumping = false;
        logger.info("Finished JvmSyncPump.");
        //throw (new RuntimeException("Not implemented"));
    }

    @Override
    public boolean isPumping() {
        return this.isPumping;
    }

}
