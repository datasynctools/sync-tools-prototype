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
package tools.datasync.api.dao;

import java.util.Iterator;
import java.util.List;

import tools.datasync.api.msg.SyncEntityMessage;

public interface GenericDao {

    public Iterator<SyncEntityMessage> selectAll(String entityName,
	    boolean sorted) throws Exception;

    public void saveOrUpdate(String entityName,
	    List<SyncEntityMessage> jsonList, String keyColumn)
	    throws Exception;

    public void saveOrUpdate(String entityName, SyncEntityMessage json,
	    String keyColumn) throws Exception;

    public void save(String entityName, SyncEntityMessage json)
	    throws Exception;

    public void update(String entityName, SyncEntityMessage json,
	    String keyColumn) throws Exception;

    public SyncEntityMessage select(String tableName, String recordId)
	    throws Exception;

    public SyncEntityMessage selectState(String entityId, String recordId)
	    throws Exception;
}
