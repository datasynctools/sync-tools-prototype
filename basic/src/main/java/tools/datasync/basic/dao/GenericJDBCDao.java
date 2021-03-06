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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import tools.datasync.basic.model.Ids;
import tools.datasync.basic.model.JSON;
import tools.datasync.basic.util.SQLGenUtil;

public class GenericJDBCDao implements GenericDao {

	private DataSource dataSource;
	private Logger logger = Logger.getLogger(GenericJDBCDao.class.getName());
	private String dbName;

	public GenericJDBCDao() {
		// TODO: create instance of data source
	}

	public void setDataSource(DataSource dataSource, String dbName) {
		this.dataSource = dataSource;
		this.dbName = dbName;
	}

	// Returning result set linked iterator because size of database can cause
	// out of memory error.
	public Iterator<JSON> selectAll(final String entityName, boolean sorted) throws SQLException {

		try {
			String query = "select * from " + entityName;
			if (sorted) {
				query = query + " order by " + Ids.KeyColumn.get(entityName);
			}
			final Connection connection = dataSource.getConnection();

			final Statement statement = connection.createStatement();
			logger.debug(dbName + ": " + query);
			final ResultSet result = statement.executeQuery(query);

			String primaryKey = Ids.KeyColumn.get(entityName);
			String[] keys = primaryKey.split(",");
			final List<String> primaryKeyColumns = new ArrayList<String>();
			for (String key : keys) {
				primaryKeyColumns.add(key.trim());
			}
			Collections.sort(primaryKeyColumns);

			return new Iterator<JSON>() {
				private Logger logger = Logger.getLogger("SelectAllIterator");
				boolean hasMore = false;

				public boolean hasNext() {
					try {
						hasMore = result.next();
						return hasMore;
					} catch (SQLException e) {
						logger.error(dbName + ": " + "result set error - hasNext().", e);
						return false;
					}
				}

				public JSON next() {
					try {

						JSON json = new JSON(entityName);

						StringBuffer sbPrimaryKey = new StringBuffer();
						for (String pkColumn : primaryKeyColumns) {
							String key = result.getString(pkColumn);
							sbPrimaryKey.append(key);
							sbPrimaryKey.append("->");
						}
						if (sbPrimaryKey.length() > 2) {
							sbPrimaryKey.setLength(sbPrimaryKey.length() - 2);
						}
						json.setCalculatedPrimaryKey(sbPrimaryKey.toString());
						// logger.debug("ResultSet.next() - calculated primary key: "
						// + json.getCalculatedPrimaryKey());

						int count = result.getMetaData().getColumnCount();
						for (int index = 1; index <= count; index++) {
							String columnName = result.getMetaData().getColumnName(index);
							Object value = result.getObject(index);

							json.set(columnName.toUpperCase(), value);
						}
						count++;
						logger.debug(dbName + ": ResultSet.next() - returning " + entityName + " - " + json);
						return json;
					} catch (SQLException e) {
						logger.warn(dbName + ": result set error - next().", e);
						throw new RuntimeException(e);
					} finally {
						try {
							if (!hasMore) {
								logger.debug(dbName + ": selectAll() - closing resultset");
								result.close();
								statement.close();
								connection.close();
							}
						} catch (SQLException e) {
							logger.warn(dbName + ": error while closing result set.", e);
						}
					}
				}

				public void remove() {
					// TODO: implement;
				}
			};

		} catch (SQLException e) {
			logger.warn(dbName + ": SQL error:", e);
			throw e;
		}
	}

	public JSON select(final String entityName, String id) throws SQLException {

		Connection connection = null;
		Statement statement = null;
		ResultSet result = null;
		try {
			String query = "select * from " + entityName + " where " + Ids.KeyColumn.get(entityName) + "='" + id + "'";
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			logger.debug(dbName + ": " + query);
			result = statement.executeQuery(query);

			if (result.next()) {

				JSON json = new JSON(entityName);
				int count = result.getMetaData().getColumnCount();
				for (int index = 1; index <= count; index++) {
					String columnName = result.getMetaData().getColumnName(index);
					Object value = result.getObject(index);

					json.set(columnName.toUpperCase(), value);
				}

				return json;
			} else {
				return null;
			}

		} catch (SQLException e) {
			logger.warn(dbName + ": " + "result set error - select().", e);
			throw new RuntimeException(e);
		} finally {
			result.close();
			statement.close();
			connection.close();
		}

	}

	public JSON selectState(String entityId, String recordId) throws SQLException {

		Connection connection = null;
		Statement statement = null;
		ResultSet result = null;
		try {
			String query = "select * from " + Ids.Table.SYNC_STATE + " where EntityId='" + entityId + "' and RecordId='" + recordId + "'";
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			logger.debug(dbName + ": " + query);
			result = statement.executeQuery(query);

			if (result.next()) {

				JSON json = new JSON(Ids.Table.SYNC_STATE);
				int count = result.getMetaData().getColumnCount();
				for (int index = 1; index <= count; index++) {
					String columnName = result.getMetaData().getColumnName(index);
					Object value = result.getObject(index);

					json.set(columnName.toUpperCase(), value);
				}

				return json;
			} else {
				return null;
			}

		} catch (SQLException e) {
			logger.warn(dbName + ": " + "result set error - select().", e);
			throw new RuntimeException(e);
		} finally {
			result.close();
			statement.close();
			connection.close();
		}

	}

	public void save(String entityName, JSON json) throws SQLException {

		// logger.debug("entityName=" + entityName + ", json=" + json);
		Connection connection = null;
		Statement statement = null;
		try {
			// Try insert statement...
			String insert = SQLGenUtil.getInsertStatement(entityName, json);
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			logger.debug(dbName + ": " + insert);
			statement.execute(insert);
			logger.debug(dbName + ": " + "commiting insert...");
			connection.commit();
		} finally {
			if (statement != null) {
				try {
					statement.close();
					connection.close();
				} catch (SQLException e) {
					logger.warn(dbName + ": " + "Failed to close connection.", e);
				}
			}
		}
	}
	
	public void update(String entityName, JSON json, String keyColumn) throws SQLException {

		logger.debug(dbName + ": update() - entityName=" + entityName + ", json=" + json + ", keyColumn=" + keyColumn);
		Connection connection = null;
		Statement statement = null;
		try {
			// Try update statement...
			String update = SQLGenUtil.getUpdateStatement(entityName, json, keyColumn);
			logger.debug(dbName + ": update() - " + update);
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			statement.executeUpdate(update);
			
			logger.debug(dbName + ": " + "commiting update...");
			connection.commit();
		} finally {
			if (statement != null) {
				try {
					statement.close();
					connection.close();
				} catch (SQLException e) {
					logger.warn(dbName + ": " + "Failed to close connection.", e);
				}
			}
		}
	}

	public void saveOrUpdate(String entityName, JSON json, String keyColumn) throws SQLException {

		logger.debug(dbName + ": saveOrUpdate() - entityName=" + entityName + ", json=" + json + ", keyColumn=" + keyColumn);
		Connection connection = null;
		Statement statement = null;
		try {
			// Try insert statement first...
			String insert = SQLGenUtil.getInsertStatement(entityName, json);
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			logger.debug(dbName + ": saveOrUpdate() - " + insert);
			statement.execute(insert);
		} catch (SQLException ex) {
			// May be primary key violation, try update statement...
			if (SQLGenUtil.isConstraintViolation(ex)) {
				try {
					String update = SQLGenUtil.getUpdateStatement(entityName, json, keyColumn);
					logger.debug(dbName + ": saveOrUpdate() - " + update);
					statement.execute(update);
				} catch (SQLException e) {
					logger.warn(dbName + ": Failed to update record", e);
					throw ex;
				}
			} else {
				logger.error(dbName + ": Failed to insert record", ex);
				throw ex;
			}
		} finally {
			if (statement != null) {
				try {
					logger.debug(dbName + ": saveOrUpdate() - commiting changes.");
					connection.commit();
					statement.close();
					connection.close();
				} catch (SQLException e) {
					logger.warn(dbName + ": Failed to close connection.", e);
				}
			}
		}
	}

	public void saveOrUpdate(String entityName, List<JSON> jsonList, String keyColumn) throws SQLException {
		logger.debug(dbName + ": saveOrUpdate() - entityName=" + entityName + ", count=" + jsonList.size() + ", keyColumn=" + keyColumn);
		for (JSON json : jsonList) {
			this.saveOrUpdate(entityName, json, keyColumn);
		}
	}

	public String getSyncRecordId(JSON json) {

		String entityName = json.getEntity();

		// TODO: Implement...
		return null;
	}
}
