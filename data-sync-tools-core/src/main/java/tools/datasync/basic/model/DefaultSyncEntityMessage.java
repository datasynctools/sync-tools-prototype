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
 * @since   20-Nov-2014
 */

package tools.datasync.basic.model;

import java.io.Serializable;
import java.util.Map;

import tools.datasync.api.msg.SyncEntityMessage;
import tools.datasync.api.msg.SyncPayloadData;

//TODO Doug comment: While I see what this class is doing at a high level, I think it can be further reduced.
//For instance, it can be altered to reduce the number of non-native objects being created and 
//to better support a pluggable data format.
public class DefaultSyncEntityMessage extends SyncPayloadData implements
	SyncEntityMessage, Cloneable, Serializable {

    private static final long serialVersionUID = 1052072136660446741L;

    private String entity;
    private String calculatedPrimaryKey;
    private SyncEntityMessageSupport support;

    public DefaultSyncEntityMessage() {
	support = new SyncEntityMessageSupport();
    }

    public void set(String name, Object value) {
	support.set(name, value);
    }

    public Object get(String name) {
	return support.get(name);

    }

    public String getType(String name) {
	return support.getType(name);
    }

    public String getEntity() {
	return entity;
    }

    public void setEntity(String entity) {
	this.entity = entity;
    }

    public String getCalculatedPrimaryKey() {
	return calculatedPrimaryKey;
    }

    public void setCalculatedPrimaryKey(String calculatedPrimaryKey) {
	this.calculatedPrimaryKey = calculatedPrimaryKey;
    }

    public Map<String, Object> getData() {
	return support.getData();
    }

    public void setData(Map<String, Object> props) {
	support.setData(props);
    }

    public Map<String, String> getTypes() {
	return support.getTypes();
    }

    public void setTypes(Map<String, String> types) {
	support.setTypes(types);
    }

    @Override
    public String toString() {
	return String.valueOf(support.getData());
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((entity == null) ? 0 : entity.hashCode());

	Map<String, Object> props = support.getData();

	result = prime * result + ((props == null) ? 0 : props.hashCode());
	return result;
    }

    // TODO Is this needed given the current implementation?
    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;

	DefaultSyncEntityMessage other = (DefaultSyncEntityMessage) obj;
	return equals(this, other);
    }

    // TODO Is this approach still needed given the current implementation?
    private boolean equals(DefaultSyncEntityMessage me,
	    DefaultSyncEntityMessage other) {
	Map<String, Object> myProps = support.getData();
	Map<String, Object> otherProps = other.getData();
	if (myProps == null) {
	    if (otherProps != null) {
		return false;
	    }
	} else if (!myProps.equals(otherProps)) {
	    return false;
	}
	return true;
    }
}
