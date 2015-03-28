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
package tools.datasync.dao.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.apache.derby.jdbc.EmbeddedDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.api.dao.EntityGetter;
import tools.datasync.api.dao.IdGetter;
import tools.datasync.basic.model.SyncEntityMessage;
import tools.datasync.dao.GenericDao;
import tools.datasync.dao.SyncEntityMessageResultMapper;
import tools.datasync.utils.SqlGenUtil;
import tools.datasync.utils.StringUtils;

public class GenericJdbcDao implements GenericDao {

    private static final Logger LOG = LoggerFactory
	    .getLogger(GenericJdbcDao.class);

    private DataSource dataSource;
    private String dbName;
    private EntityGetter entityGetter;
    private IdGetter idGetter;

    private JdbcSelectionHelper<SyncEntityMessage> stateSelector;
    private JdbcSelectionHelper<Iterator<SyncEntityMessage>> allSelector;

    private SyncEntityMessageResultMapper resultMapper = new SyncEntityMessageResultMapper();
    private SyncEntityMessageIteratorResultMapper iteratorResultMapper;

    private JdbcMutationHelper jdbcMutator;

    public GenericJdbcDao(DataSource dataSource, String dbName,
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
	iteratorResultMapper = new SyncEntityMessageIteratorResultMapper(
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

	return (allSelector.query(query, iteratorResultMapper, entityName,
		entityName));

    }

    public SyncEntityMessage select(final String entityName, String id)
	    throws SQLException {

	String query = "select * from " + entityName + " where "
		+ idGetter.get(entityName) + "='" + id + "'";
	return (stateSelector.query(query, resultMapper, id, entityName));

    }

    public SyncEntityMessage selectState(String entityId, String recordId)
	    throws SQLException {

	String query = "select * from " + entityGetter.getSyncStateName()
		+ " where EntityId='" + entityId + "' and RecordId='"
		+ recordId + "'";
	return (stateSelector.query(query, resultMapper, recordId,
		entityGetter.getSyncStateName()));

    }

    public void save(String entityName, SyncEntityMessage recordData)
	    throws SQLException {

	Connection connection = null;
	Statement statement = null;
	try {
	    // Try insert statement...
	    String insert = SqlGenUtil.getInsertStatement(entityName,
		    recordData);
	    connection = dataSource.getConnection();

	    statement = connection.createStatement();
	    LOG.debug("db [{}] : {}", dbName, insert);
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

    public void update(String entityName, SyncEntityMessage syncEntityMsg,
	    String keyColumn) throws SQLException {

	LOG.debug(dbName + ": update() - entityName=" + entityName
		+ ", syncEntityMsg=" + syncEntityMsg + ", keyColumn="
		+ keyColumn);
	Connection connection = null;
	Statement statement = null;

	try {
	    // Try update statement...
	    String update = SqlGenUtil.getUpdateStatement(entityName,
		    syncEntityMsg, keyColumn);
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

    public void saveOrUpdate(String entityName, SyncEntityMessage syncEntityMsg,
	    String keyColumn) throws SQLException {

	jdbcMutator.saveOrUpdate(entityName, entityName, syncEntityMsg, keyColumn);

    }

    public void saveOrUpdate(String entityName,
	    List<SyncEntityMessage> syncEntityMsgList, String keyColumn)
	    throws SQLException {
	LOG.debug(dbName + ": saveOrUpdate() - entityName=" + entityName
		+ ", count=" + syncEntityMsgList.size() + ", keyColumn=" + keyColumn);
	for (SyncEntityMessage syncEntityMsg : syncEntityMsgList) {
	    this.saveOrUpdate(entityName, syncEntityMsg, keyColumn);
	}
    }

    private void addDerbyEmbddedDataSource(StringBuilder answer) {
	EmbeddedDataSource embeddedDs = (EmbeddedDataSource) dataSource;

	answer.append(StringUtils.getSimpleName(dataSource));
	answer.append("{");
	answer.append("databaseName=");
	answer.append(embeddedDs.getDatabaseName());
	answer.append(", ");
	answer.append("dataSourceName=");
	answer.append(embeddedDs.getDataSourceName());
	answer.append(", ");
	answer.append("connectionAttributes=");
	answer.append(embeddedDs.getConnectionAttributes());
	answer.append("}");
    }

    private void addDataSource(StringBuilder answer) {
	answer.append("dataSource=");
	if (dataSource.getClass().getName()
		.equals("org.apache.derby.jdbc.EmbeddedDataSource")) {
	    addDerbyEmbddedDataSource(answer);
	} else {
	    answer.append(dataSource);
	}
	answer.append(", ");
    }

    public String toString() {
	StringBuilder answer = new StringBuilder();
	answer.append(StringUtils.getSimpleName(this));
	answer.append("{");
	answer.append("dbName=");
	answer.append(dbName);
	answer.append(", ");
	addDataSource(answer);
	answer.append("idGetter=");
	answer.append(idGetter);
	answer.append(", ");
	answer.append("entityGetter=");
	answer.append(entityGetter);
	answer.append("}");
	return (answer.toString());
    }
}
