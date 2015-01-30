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
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import tools.datasync.basic.util.HashGenerator;
import tools.datasync.basic.util.Md5HashGenerator;

public class JSON implements Cloneable, Serializable {

	private static final long serialVersionUID = 1052072136660446741L;
	private static final HashGenerator hashGenerator = Md5HashGenerator.getInstance();
	private static final Logger logger = Logger.getLogger(JSON.class);
	
	private String entity;
	private String calculatedPrimaryKey;
	private Map<String, Object> props;
	private Map<String, String> types;
	
    public JSON() {
    }
    
	public JSON(String entity) {

		this.setEntity(entity);
		// Linked hash map to keep the order.
		this.props = new LinkedHashMap<String, Object>();
		this.types = new LinkedHashMap<String, String>();
	}
	
	public void set(String name, Object value){
		
	    if(value == null || "".equals(value)){
	        return;
	    }
	    if(value instanceof Date){
	        value = ((Date) value).getTime();
	    }
		this.props.put(name, value);
		this.types.put(name, value.getClass().getSimpleName());
	}
	
	public Object get(String name){
		
	    String type = this.types.get(name);
	    Object ret = null;
	    String value = String.valueOf(this.props.get(name));
	    
	    if("String".equalsIgnoreCase(type)){
	        ret = value;
	    } else if("Integer".equalsIgnoreCase(type)){
            ret = Integer.valueOf(value);
        }  else if("Long".equalsIgnoreCase(type)){
            ret = Long.valueOf(value);
        }  else if("Double".equalsIgnoreCase(type)){
            ret = Double.valueOf(value);
        }  else if("Boolean".equalsIgnoreCase(type)){
            ret = Boolean.valueOf(value);
        } else if("Date".equalsIgnoreCase(type)){
            ret = new Date(Long.valueOf(value));
        } else {
            throw new IllegalArgumentException("Type not supported "+type+" for value "+value + ", for name " + name);
        }
	    
		return ret;
	}
	
	public String getType(String name){
        
        return types.get(name);
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
    
	public Map<String, Object> getData(){
		
		return this.props;
	}
	
	public void setData(Map<String, Object> props){
        
        this.props = props;
    }
	
	public Map<String, String>  getTypes() {
        return types;
    }

    public void setTypes(Map<String, String>  types) {
        this.types = types;
    }

	@Override
	public String toString() {
		return String.valueOf(props);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entity == null) ? 0 : entity.hashCode());
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
		
		JSON other = (JSON) obj;
		if (props == null) {
			if (other.props != null)
				return false;
		} else if (!props.equals(other.props))
			return false;
		
		return true;
	}

	public String generateHash() {

		if(props == null || props.size() == 0){
			return "NO_DATA:NO_HASH";
		}
		
		List<String> keys = new ArrayList<String>();
		keys.addAll(props.keySet());
		Collections.sort(keys);
		
		StringBuffer sbValue = new StringBuffer();
		for(String key1 : keys){
			Object value = props.get(key1);
			sbValue.append(String.valueOf(value));
			sbValue.append(',');
		}
		if(sbValue.length()>0){
			sbValue.setLength(sbValue.length()-1);
		}
		String hash = hashGenerator.generate(sbValue.toString());
		logger.debug("Generated hash: " + hash +", for data: [" + sbValue.toString() + "]");
		return hash;
	}
	
}
