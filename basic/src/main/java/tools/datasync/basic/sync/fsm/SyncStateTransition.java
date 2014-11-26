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
* @copyright datasync.tools
* @version 1.0
* @since   15-Nov-2014
*/

package tools.datasync.basic.sync.fsm;

import tools.datasync.basic.comm.SyncMessageType;

public class SyncStateTransition {

	final SyncStateElement fromState;
	final SyncMessageType message;
	final SyncStateElement nextState;
	
	/**
	 * @param fromState from state
	 * @param message message
	 * @param nextState next state
	 */
	public SyncStateTransition(SyncStateElement fromState, SyncMessageType message, SyncStateElement nextState) {
		super();
		this.fromState = fromState;
		this.message = message;
		this.nextState = nextState;
	}
}
