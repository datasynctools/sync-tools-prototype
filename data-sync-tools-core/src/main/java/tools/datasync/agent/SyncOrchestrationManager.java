/**
 * 
 */
package tools.datasync.agent;

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
public class SyncOrchestrationManager {

    private SyncPumpFactory pumpFactoryA2B;
    private SyncPumpFactory pumpFactoryB2A;

    public SyncOrchestrationManager(SyncPumpFactory pumpFactoryA2B,
	    SyncPumpFactory pumpFactoryB2A) {
	this.pumpFactoryA2B = pumpFactoryA2B;
	this.pumpFactoryB2A = pumpFactoryB2A;
    }

    public SyncSession createSession() throws InstantiationException {
	SyncPump pumpA2B = pumpFactoryA2B.getInstance(PeerMode.A2B);
	SyncPump pumpB2A = pumpFactoryB2A.getInstance(PeerMode.B2A);

	return new SyncSession(pumpA2B, pumpB2A);
    }
}
