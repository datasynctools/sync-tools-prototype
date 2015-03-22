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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.basic.model.EntityGetter;
import tools.datasync.basic.model.IdGetter;
import tools.datasync.basic.model.SyncEntityMessage;
import tools.datasync.basic.util.SqlGenUtil;

public class GenericJDBCDao implements GenericDao {

    private static final Logger LOG = LoggerFactory
	    .getLogger(GenericJDBCDao.class);

    private DataSource dataSource;
    private String dbName;
    private EntityGetter entityGetter;
    private IdGetter idGetter;

    private JdbcSelectionHelper<SyncEntityMessage> stateSelector;
    private JdbcSelectionHelper<Iterator<SyncEntityMessage>> allSelector;

    private SyncEntityMessageResultMapper jsonResultMapper = new SyncEntityMessageResultMapper();
    private SyncEntityMessageIteratorResultMapper jsonIteratorResultMapper;

    private JdbcMutationHelper jdbcMutator;

    public GenericJDBCDao(DataSource dataSource, String dbName,
	    EntityGetter entityGetter, IdGetter idGetter) {
	this.dataSource = dataSource;
	this.dbName = dbName;
	this.entityGetter = entityGetter;
	this.idGetter = idGetter;
	stateSelector = new JdbcSelectionHelper<SyncEntityMessage>(dataSource);
	allSelector = new JdbcSelectionHelper<Iterator<SyncEntityMessage>>(
		dataSource);
	jdbcMutator = new JdbcMutationHelper(dataSource,
		new InsertSqlCreator(), new UpdateSqlCreator());
	jsonIteratorResultMapper = new SyncEntityMessageIteratorResultMapper(
		idGetter);
    }

    // Returning result set linked iterator because size of database can cause
    // out of memory error.
    public Iterator<SyncEntityMessage> selectAll(final String entityName,
	    boolean sorted) throws SQLException {

	String query = "select * from " + entityName;
	if (sorted) {
	    // query = query + " order by " + Ids.KeyColumn.get(entityName);
	    String primaryKey = idGetter.get(entityName);
	    String[] keys = primaryKey.split(",");

	    query = query + " order by " + keys[0];
	    for (int x = 1; (x + 1) <= keys.length; x++) {
		query = query + ", " + keys[x];
	    }
	}

	return (allSelector.query(query, jsonIteratorResultMapper, entityName,
		entityName));

    }

    public SyncEntityMessage select(final String entityName, String id)
	    throws SQLException {

	String query = "select * from " + entityName + " where "
		+ idGetter.get(entityName) + "='" + id + "'";
	return (stateSelector.query(query, jsonResultMapper, id, entityName));

    }

    public SyncEntityMessage selectState(String entityId, String recordId)
	    throws SQLException {

	String query = "select * from " + entityGetter.getSyncStateName()
		+ " where EntityId='" + entityId + "' and RecordId='"
		+ recordId + "'";
	return (stateSelector.query(query, jsonResultMapper, recordId,
		entityGetter.getSyncStateName()));

    }

    public void save(String entityName, SyncEntityMessage recordData)
	    throws SQLException {

	Connection connection = null;
	Statement statement = null;
	try {
	    // Try insert statement...
	    String insert = SqlGenUtil.getInsertStatement(entityName, recordData);
	    connection = dataSource.getConnection();

	    statement = connection.createStatement();
	    LOG.info("db [{}] : {}", dbName, insert);
	    statement.execute(insert);
	    // LOG.debug(dbName + ": " + "commiting insert...");
	    // LOG.debug("db [{}] : {}", dbName, insert);
	    connection.commit();
	} finally {
	    if (statement != null) {
		try {
		    statement.close();
		    connection.close();
		} catch (SQLException e) {
		    LOG.warn(dbName + ": " + "Failed to close connection.", e);
		}
	    }
	}
    }

    public void update(String entityName, SyncEntityMessage json,
	    String keyColumn) throws SQLException {

	LOG.debug(dbName + ": update() - entityName=" + entityName + ", json="
		+ json + ", keyColumn=" + keyColumn);
	Connection connection = null;
	Statement statement = null;

	try {
	    // Try update statement...
	    String update = SqlGenUtil.getUpdateStatement(entityName, json,
		    keyColumn);
	    LOG.debug(dbName + ": update() - " + update);
	    connection = dataSource.getConnection();
	    statement = connection.createStatement();
	    statement.executeUpdate(update);

	    LOG.debug(dbName + ": " + "commiting update...");
	    connection.commit();

	} finally {

	    JdbcCloseUtils.closeRuntimeException(connection, statement);

	}
    }

    public void saveOrUpdate(String entityName, SyncEntityMessage json,
	    String keyColumn) throws SQLException {

	jdbcMutator.saveOrUpdate(entityName, entityName, json, keyColumn);

    }

    public void saveOrUpdate(String entityName,
	    List<SyncEntityMessage> jsonList, String keyColumn)
	    throws SQLException {
	LOG.debug(dbName + ": saveOrUpdate() - entityName=" + entityName
		+ ", count=" + jsonList.size() + ", keyColumn=" + keyColumn);
	for (SyncEntityMessage json : jsonList) {
	    this.saveOrUpdate(entityName, json, keyColumn);
	}
    }

    public String getSyncRecordId(SyncEntityMessage json) {

	// String entityName = json.getEntity();

	// TODO: Implement...
	return null;
    }
}
