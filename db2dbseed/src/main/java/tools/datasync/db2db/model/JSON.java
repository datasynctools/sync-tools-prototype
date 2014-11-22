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

package tools.datasync.db2db.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class JSON implements Cloneable, Serializable {

	private static final long serialVersionUID = 1052072136660446741L;
	private String entity;
	private Map<String, Object> props;
	
	public JSON(String entity) {

		this.setEntity(entity);
		// Linked hash map to keep the order.
		this.props = new LinkedHashMap<String, Object>();
	}
	
	public void set(String name, Object value){
		
		this.props.put(name, value);
	}
	
	public Object get(String name){
		
		return this.props.get(name);
	}
	
	public Set<String> getAllNames(){
		
		return this.props.keySet();
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}
	
	@Override
	public String toString() {
		return String.valueOf(props);
	}
}
