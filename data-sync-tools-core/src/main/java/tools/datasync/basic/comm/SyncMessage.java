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

package tools.datasync.basic.comm;

import java.io.Serializable;

public class SyncMessage implements Serializable, Cloneable {

    private static final long serialVersionUID = -3180554612753110701L;

    private String originId; // peer id
    private long messageNumber; // required for ACK/ NACK
    private String messageType;
    private String payloadJson;
    private String paloadHash;
    private long timestamp;

    public SyncMessage() {
    }

    // /**
    // * @param originId
    // * @param messageNumber
    // * @param messageType
    // * @param payloadJson
    // * @param paloadHash
    // * @param timestamp
    // */
    // public SyncMessage(String originId, long messageNumber, String
    // messageType, String payloadJson, String paloadHash, long timestamp) {
    // super();
    // this.originId = originId;
    // this.messageNumber = messageNumber;
    // this.messageType = messageType;
    // this.payloadJson = payloadJson;
    // this.paloadHash = paloadHash;
    // this.timestamp = timestamp;
    // }

    /**
     * @return the originId
     */
    public String getOriginId() {
	return originId;
    }

    /**
     * @param originId
     *            the originId to set
     */
    public void setOriginId(String originId) {
	this.originId = originId;
    }

    /**
     * @return the messageNumber
     */
    public long getMessageNumber() {
	return messageNumber;
    }

    /**
     * @param messageNumber
     *            the messageNumber to set
     */
    public void setMessageNumber(long messageNumber) {
	this.messageNumber = messageNumber;
    }

    /**
     * @return the messageType
     */
    public String getMessageType() {
	return messageType;
    }

    /**
     * @param messageType
     *            the messageType to set
     */
    public void setMessageType(String messageType) {
	this.messageType = messageType;
    }

    /**
     * @return the recordJson
     */
    public String getPayloadJson() {
	return payloadJson;
    }

    /**
     * @param recordJson
     *            the recordJson to set
     */
    public void setPayloadJson(String payloadJson) {
	this.payloadJson = payloadJson;
    }

    /**
     * @return the paloadHash
     */
    public String getPaloadHash() {
	return paloadHash;
    }

    /**
     * @param paloadHash
     *            the paloadHash to set
     */
    public void setPayloadHash(String paloadHash) {
	this.paloadHash = paloadHash;
    }

    /**
     * @return the timestamp
     */
    public long getTimestamp() {
	return timestamp;
    }

    /**
     * @param timestamp
     *            the timestamp to set
     */
    public void setTimestamp(long timestamp) {
	this.timestamp = timestamp;
    }

    /**
     * @return the serialversionuid
     */
    public static long getSerialversionuid() {
	return serialVersionUID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "SyncMessage [originId=" + originId + ", messageNumber="
		+ messageNumber + ", messageType=" + messageType
		+ ", payloadJson=" + payloadJson + ", paloadHash=" + paloadHash
		+ ", timestamp=" + timestamp + "]";
    }
}
