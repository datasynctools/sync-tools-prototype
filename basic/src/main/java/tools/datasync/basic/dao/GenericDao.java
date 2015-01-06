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
package tools.datasync.basic.dao;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import tools.datasync.basic.model.JSON;

public interface GenericDao {

	public Iterator<JSON> selectAll(String entityName) throws SQLException;
	
	public void saveOrUpdate(String entityName, List<JSON> jsonList, String keyColumn) throws SQLException;
	
	public void saveOrUpdate(String entityName, JSON json, String keyColumn) throws SQLException;

    public void save(String entityName, JSON json) throws SQLException;

	public JSON select(String tableName, String recordId) throws SQLException;
	
	public JSON selectState(String entityId, String recordId) throws SQLException;
}
