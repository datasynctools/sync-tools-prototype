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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.basic.util.HashGenerator;
import tools.datasync.basic.util.Md5HashGenerator;

//TODO Doug comment: While I see what this class is doing at a high level, I think it can be further reduced.
//For instance, it can be altered to reduce the number of non-native objects being created and 
//to better support a pluggable data format.
public class SyncEntityMessage implements Cloneable, Serializable {

    private static final Logger LOG = LoggerFactory
	    .getLogger(SyncEntityMessage.class);

    private static final long serialVersionUID = 1052072136660446741L;
    private static final HashGenerator hashGenerator = Md5HashGenerator
	    .getInstance();

    private String entity;
    private String calculatedPrimaryKey;
    private SyncEntityMessageSupport support;

    public SyncEntityMessage() {
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

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;

	SyncEntityMessage other = (SyncEntityMessage) obj;
	return equals(this, other);
    }

    private boolean equals(SyncEntityMessage me, SyncEntityMessage other) {
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

    private List<String> sortProps() {
	List<String> keys = new ArrayList<String>();
	keys.addAll(support.getData().keySet());
	Collections.sort(keys);
	return keys;
    }

    private StringBuffer flattenProps(List<String> sortedKeys) {
	StringBuffer sbValue = new StringBuffer();
	for (String key : sortedKeys) {
	    Object value = support.getData().get(key);
	    sbValue.append(String.valueOf(value));
	    sbValue.append(',');
	}
	if (sbValue.length() > 0) {
	    sbValue.setLength(sbValue.length() - 1);
	}
	return sbValue;
    }

    public String generateHash() {
	Map<String, Object> props = support.getData();
	if (props == null || props.size() == 0) {
	    return "NO_DATA:NO_HASH";
	}

	List<String> keys = sortProps();

	StringBuffer sbValue = flattenProps(keys);

	String hash = hashGenerator.generate(sbValue.toString());
	LOG.debug("Generated hash: " + hash + ", for data: ["
		+ sbValue.toString() + "]");
	return hash;
    }

}
