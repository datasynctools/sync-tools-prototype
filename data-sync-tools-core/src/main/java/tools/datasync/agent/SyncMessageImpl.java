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
 * @since   12-Nov-2014
 */

package tools.datasync.agent;

import java.io.Serializable;

import tools.datasync.api.agent.SyncMessage;

public class SyncMessageImpl implements SyncMessage, Serializable, Cloneable {

    private static final long serialVersionUID = -3180554612753110701L;

    private SyncMessageHeader header;
    private SyncMessagePayload payload;

    public SyncMessageImpl(SyncMessageHeader header, SyncMessagePayload payload) {
	super();
	this.header = header;
	this.payload = payload;
    }

    public String getOriginId() {
	return header.getOriginId();
    }

    public long getMessageNumber() {
	return header.getMessageNumber();
    }

    public String getMessageType() {
	return header.getMessageType();
    }

    public String getPayloadJson() {
	return payload.getPayloadJson();
    }

    public String getPayloadHash() {
	return payload.getPayloadHash();
    }

    public long getTimestamp() {
	return header.getTimestamp();
    }

    public String toString() {
	return "SyncMessage [originId=" + header.getOriginId()
		+ ", messageNumber=" + header.getMessageNumber()
		+ ", messageType=" + header.getMessageType() + ", payloadJson="
		+ payload.getPayloadJson() + ", paloadHash="
		+ payload.getPayloadHash() + ", timestamp="
		+ header.getTimestamp() + "]";
    }
}
