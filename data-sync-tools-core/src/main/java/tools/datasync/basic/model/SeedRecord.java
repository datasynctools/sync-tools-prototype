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
 * @version 1.0
 * @since   2014-11-10
 */
package tools.datasync.basic.model;

import java.io.Serializable;

public class SeedRecord implements Serializable, Cloneable {

    private static final long serialVersionUID = 2953233347238587733L;
    private String entityId;
    private String recordId;
    private String recordHash;
    private String recordJson;
    private String origin;

    public SeedRecord() {
    }

    /**
     * @return the entityId
     */
    public String getEntityId() {
	return entityId;
    }

    /**
     * @param entityId
     *            the entityId to set
     */
    public void setEntityId(String entityId) {
	this.entityId = entityId;
    }

    /**
     * @return the recordId
     */
    public String getRecordId() {
	return recordId;
    }

    /**
     * @param recordId
     *            the recordId to set
     */
    public void setRecordId(String recordId) {
	this.recordId = recordId;
    }

    /**
     * @return the recordHash
     */
    public String getRecordHash() {
	return recordHash;
    }

    /**
     * @param recordHash
     *            the recordHash to set
     */
    public void setRecordHash(String recordHash) {
	this.recordHash = recordHash;
    }

    /**
     * @return the recordJson
     */
    public String getRecordJson() {
	return recordJson;
    }

    /**
     * @param recordJson
     *            the recordJson to set
     */
    public void setRecordJson(String recordJson) {
	this.recordJson = recordJson;
    }

    /**
     * @return the origin
     */
    public String getOrigin() {
	return origin;
    }

    /**
     * @param origin
     *            the origin to set
     */
    public void setOrigin(String origin) {
	this.origin = origin;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "SeedRecord [entityId=" + entityId + ", recordId=" + recordId
		+ ", recordHash=" + recordHash + ", recordJson=" + recordJson
		+ ", origin=" + origin + "]";
    }

}
