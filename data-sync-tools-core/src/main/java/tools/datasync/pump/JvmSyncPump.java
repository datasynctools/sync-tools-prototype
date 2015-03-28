/**
 * 
 */
package tools.datasync.pump;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.api.PeerMode;

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

    private static final Logger LOG = LoggerFactory
	    .getLogger(JvmSyncPump.class);

    private PeerMode peerMode;
    private JvmSyncPumpSender sender = null;
    private JvmSyncPumpReceiver receiver = null;

    public JvmSyncPump(PeerMode peerMode, JvmSyncPumpSender sender,
	    JvmSyncPumpReceiver receiver) {
	this.peerMode = peerMode;
	this.sender = sender;
	this.receiver = receiver;
    }

    public void beginPump() {
	Thread senderThread = new Thread(sender, "Sender-"
		+ this.peerMode.name());
	senderThread.setUncaughtExceptionHandler(sender);
	Thread receiverThread = new Thread(receiver, "Receiver-"
		+ this.peerMode.name());
	receiverThread.setUncaughtExceptionHandler(receiver);
	senderThread.start();
	receiverThread.start();

	LOG.info("Started JvmSyncPump sender and receiver");
    }

    public boolean isPumping() {
	return sender.isRunning().get() || receiver.isRunning().get();
    }

}
