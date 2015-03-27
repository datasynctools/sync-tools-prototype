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

package tools.datasync.api.msg;

import java.io.Serializable;

public class SyncMessage implements Serializable, Cloneable {

    private static final long serialVersionUID = -3180554612753110701L;

    private String originId = ""; // TODO populate the peer id
    private long messageNumber; // required for ACK/ NACK
    private String messageType;
    private SyncPayloadData payloadData = null;
    private String paloadHash = "";
    private long timestamp;

    public String getOriginId() {
	return originId;
    }

    public void setOriginId(String originId) {
	this.originId = originId;
    }

    public long getMessageNumber() {
	return messageNumber;
    }

    public void setMessageNumber(long messageNumber) {
	this.messageNumber = messageNumber;
    }

    public String getMessageType() {
	return messageType;
    }

    public void setMessageType(String messageType) {
	this.messageType = messageType;
    }

    public SyncPayloadData getPayloadData() {
	return payloadData;
    }

    public void setPayloadData(SyncPayloadData payloadData) {
	this.payloadData = payloadData;
    }

    public String getPaloadHash() {
	return paloadHash;
    }

    public void setPayloadHash(String paloadHash) {
	this.paloadHash = paloadHash;
    }

    public long getTimestamp() {
	return timestamp;
    }

    public void setTimestamp(long timestamp) {
	this.timestamp = timestamp;
    }

    public static long getSerialversionuid() {
	return serialVersionUID;
    }

    @Override
    public String toString() {
	return "SyncMessage [originId=" + originId + ", messageNumber="
		+ messageNumber + ", messageType=" + messageType
		+ ", payloadData=" + payloadData + ", paloadHash=" + paloadHash
		+ ", timestamp=" + timestamp + "]";
    }
}
