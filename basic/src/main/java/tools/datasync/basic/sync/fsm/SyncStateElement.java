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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import tools.datasync.basic.comm.SyncMessageType;

public class SyncStateElement implements Serializable, Cloneable {

	// ------------------------------------------------
	public static final SyncStateElement READY = new SyncStateElement("READY");
	
	public static final SyncStateElement SEEDING = new SyncStateElement("SEEDING");
	
	public static final SyncStateElement ANALYZING = new SyncStateElement("ANALYZING");
	
	public static final SyncStateElement WAITING = new SyncStateElement("WAITING");
	
	public static final SyncStateElement RESOLVING = new SyncStateElement("RESOLVING");
	
	public static final SyncStateElement SYNCING = new SyncStateElement("SYNCING");
	
	public static final SyncStateElement INVALID = new SyncStateElement("INVALID");
	// ------------------------------------------------
	
	private final String syncState;
	private final List<SyncStateTransition> transitions;
	private static final long serialVersionUID = -862371255991029326L;
	
	protected SyncStateElement(String state){
		this.syncState = state;
		this.transitions = new ArrayList<SyncStateTransition>();
	}

	public void addTransition(SyncStateTransition transition){
		
		this.transitions.add(transition);
	}
	
	public boolean hasTransition(SyncStateElement fromState, SyncMessageType message){
		
		for(SyncStateTransition transition : transitions){
			if(transition.fromState.equals(fromState)
					&& transition.message.equals(message)){
				return true;
			}
		}
		return false;
	}
	
	public SyncStateElement getTransition(SyncStateElement fromState, SyncMessageType message){
		
		for(SyncStateTransition transition : transitions){
			if(transition.fromState.equals(fromState)
					&& transition.message.equals(message)){
				return transition.nextState;
			}
		}
		return INVALID;
	}
	
	public List<SyncStateTransition> getTransitions(){
		List<SyncStateTransition> dest = new ArrayList<SyncStateTransition>();
		dest.addAll(transitions);
		return dest;
	}
	
	@Override
	public String toString() {
		return syncState;
	}
	
	@Override
	public int hashCode() {
		return syncState.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if( super.equals(obj)){
			return true;
		}
		if(obj instanceof SyncStateElement){
			if(this.syncState.equalsIgnoreCase(((SyncStateElement) obj).syncState)){
				return true;
			}
		}
		return false;
	}
}
