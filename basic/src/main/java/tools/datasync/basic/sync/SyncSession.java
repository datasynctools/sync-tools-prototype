package tools.datasync.basic.sync;

import org.apache.log4j.Logger;

import tools.datasync.basic.comm.SyncConnection;
import tools.datasync.basic.sync.fsm.SyncStateElement;
import tools.datasync.basic.sync.pump.JvmSyncPump;
import tools.datasync.basic.sync.pump.SyncPump;

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
public class SyncSession {

    Logger logger = Logger.getLogger(JvmSyncPump.class.getName());

    public SyncPump pumpA2B;
    public SyncPump pumpB2A;
    public SyncConnection syncConnA;
    public SyncConnection syncConnB;

    public String sessionId;
    public SyncStateElement syncStateA;
    public SyncStateElement syncStateB;

    // TODO: figure out how to monitor state of N/W based pumping strategies.
    // HeartBeat ?

    /**
     * @param syncConnA
     * @param syncConnB
     */
    public SyncSession(SyncPump pumpA2B, SyncPump pumpB2A) {
	super();
	this.pumpA2B = pumpA2B;
	this.pumpB2A = pumpB2A;
    }

    public void doSync() {
	pumpA2B.beginPump();
	pumpB2A.beginPump();
	while (pumpA2B.isPumping() || pumpB2A.isPumping()) {
	    try {
		Thread.sleep(100);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}

	logger.info("Finished sync");
    }
}
