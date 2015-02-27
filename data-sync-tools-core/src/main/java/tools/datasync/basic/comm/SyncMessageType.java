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

package tools.datasync.basic.comm;

import java.io.Serializable;

import tools.datasync.basic.util.StringUtils;

// TODO: Need state machine pattern here...
public class SyncMessageType implements Serializable, Cloneable {

	// ------------------------------------------------
	public static final SyncMessageType BEGIN_SYNC = new SyncMessageType("BEGIN_SYNC");
	public static final SyncMessageType SYNC_OVER = new SyncMessageType("SYNC_OVER");

	public static final SyncMessageType ACK = new SyncMessageType("ACK");
	public static final SyncMessageType NACK = new SyncMessageType("NACK");

	public static final SyncMessageType BEGIN_SEED = new SyncMessageType("BEGIN_SEED");
	public static final SyncMessageType SEED = new SyncMessageType("SEED");
	public static final SyncMessageType SEED_OVER_INIT = new SyncMessageType("SEED_OVER_INIT");
	public static final SyncMessageType SEED_OVER_FOLLOW = new SyncMessageType("SEED_OVER_FOLLOW");

	public static final SyncMessageType ANALYSIS_STARTED = new SyncMessageType("ANALYSIS_STARTED");
	public static final SyncMessageType ANALYSIS_OVER = new SyncMessageType("ANALYSIS_OVER");

	public static final SyncMessageType CONFLICT_RESOLUTION = new SyncMessageType("CONFLICT_RESOLUTION");

	public static final SyncMessageType APPLY_CHANGES = new SyncMessageType("APPLY_CHANGES");
	public static final SyncMessageType APPLY_CHANGES_OVER = new SyncMessageType("APPLY_CHANGES_OVER");

	public static final SyncMessageType UNKNOWN = new SyncMessageType("UNKNOWN");
	// ------------------------------------------------

	private final String messageType;
	private static final long serialVersionUID = 7007483768286153752L;

	protected SyncMessageType(String messageType) {
		this.messageType = messageType;
	}

	public static SyncMessageType get(String message) {
		SyncMessageType result = UNKNOWN;

		if(StringUtils.isEmpty(message)){
			return result;
		}
		
		if (BEGIN_SYNC.messageType.equalsIgnoreCase(message)) {
			result = BEGIN_SYNC;
		} else if (SYNC_OVER.messageType.equalsIgnoreCase(message)) {
			result = SYNC_OVER;
		} else if (ACK.messageType.equalsIgnoreCase(message)) {
			result = ACK;
		} else if (NACK.messageType.equalsIgnoreCase(message)) {
			result = NACK;
		} else if (BEGIN_SEED.messageType.equalsIgnoreCase(message)) {
			result = BEGIN_SEED;
		} else if (SEED.messageType.equalsIgnoreCase(message)) {
			result = SEED;
		} else if (SEED_OVER_INIT.messageType.equalsIgnoreCase(message)) {
			result = SEED_OVER_INIT;
		} else if (SEED_OVER_FOLLOW.messageType.equalsIgnoreCase(message)) {
			result = SEED_OVER_FOLLOW;
		} else if (ANALYSIS_STARTED.messageType.equalsIgnoreCase(message)) {
			result = ANALYSIS_STARTED;
		} else if (ANALYSIS_OVER.messageType.equalsIgnoreCase(message)) {
			result = ANALYSIS_OVER;
		} else if (CONFLICT_RESOLUTION.messageType.equalsIgnoreCase(message)) {
			result = CONFLICT_RESOLUTION;
		} else if (APPLY_CHANGES.messageType.equalsIgnoreCase(message)) {
			result = APPLY_CHANGES;
		} else if (APPLY_CHANGES_OVER.messageType.equalsIgnoreCase(message)) {
			result = APPLY_CHANGES_OVER;
		} else if (UNKNOWN.messageType.equalsIgnoreCase(message)) {
			result = UNKNOWN;
		} else {
			result = UNKNOWN;
		}
		return result;
	}
	
	@Override
	public String toString() {
		return messageType;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(super.equals(obj)){
			return true;
		}
		if(obj instanceof SyncMessageType){
			if(((SyncMessageType) obj).messageType.equalsIgnoreCase(this.messageType)){
				return true;
			}
		} else if (obj instanceof String) {
		    if(this.messageType.equalsIgnoreCase((String)obj)){
		        return true;
		    }
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return messageType.hashCode();
	}
}
