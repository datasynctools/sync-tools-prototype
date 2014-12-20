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
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import tools.datasync.basic.model.JSON;
import tools.datasync.basic.util.NLogger;
import tools.datasync.basic.util.SQLGenUtil;

public class GenericJDBCDao implements GenericDao {

	private DataSource dataSource;
	private NLogger nlogger = NLogger.getLogger();
	private Logger logger = Logger.getLogger(GenericJDBCDao.class.getName());

    public GenericJDBCDao() {
        // TODO: create instance of data source
    }
    
    public void setDataSource(DataSource dataSource){
        this.dataSource = dataSource;
    }
    
	// Returning result set linked iterator because size of database can cause
	// out of memory error.
	public Iterator<JSON> selectAll(final String entityName) {

		try {
			String query = "select * from " + entityName;
			final Connection connection = dataSource.getConnection();
			final Statement statement = connection.createStatement();
			logger.finest("selectAll() - " + query);
			final ResultSet result = statement.executeQuery(query);

			return new Iterator<JSON>() {
				boolean hasMore = false;

				public boolean hasNext() {
					try {
					    hasMore = result.next();
					    return hasMore;
					} catch (SQLException e) {
						nlogger.log(e, Level.INFO, "result set error - hasNext().");
						return false;
					}
				}

				public JSON next() {
					try {
						
						JSON json = new JSON(entityName);
						int count = result.getMetaData().getColumnCount();
						for (int index = 1; index <= count; index++) {
							String columnName = result.getMetaData().getColumnName(index);
							String value = result.getString(index);
							
							json.set(columnName, value);
						}
						count++;
						logger.info("ResultSet.next() - returning " + entityName + " - " + json);
						return json;
					} catch (SQLException e) {
						nlogger.log(e, Level.INFO, "result set error - next().");
						return null;
					} finally {
						try {
							if (! hasMore) {
								logger.finest("selectAll() - closing resultset");
								result.close();
								statement.close();
								connection.close();
							}
						} catch (SQLException e) {
							nlogger.log(e, Level.INFO, "error while closing result set.");
						}
					}
				}
				
				public void remove() {
					//TODO: implement;
				}
			};

		} catch (SQLException e) {
			nlogger.log(e, Level.INFO, "result set error.");
			List<JSON> jsonList = new ArrayList<JSON>();
			return jsonList.iterator();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tools.datasync.db2db.dao.GenericDao#saveOrUpdate(java.lang.String,
	 * tools.datasync.db2db.model.JSON)
	 */
	public void saveOrUpdate(String entityName, JSON json, String keyColumn) {

		logger.finest("saveOrUpdate() - entityName=" + entityName + ", json=" + json + ", keyColumn=" + keyColumn);
		Connection connection = null;
		Statement statement = null;
		try {
			// Try insert statement first...
			String insert = SQLGenUtil.getInsertStatement(entityName, json);
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			logger.finest("saveOrUpdate() - " + insert);
			statement.execute(insert);
		} catch (SQLException ex) {
			// May be primary key violation, try update statement...
			if (SQLGenUtil.isConstraintViolation(ex)) {
				try {
					String update = SQLGenUtil.getUpdateStatement(entityName, json, keyColumn);
					logger.finest("saveOrUpdate() - " + update);
					statement.execute(update);
				} catch (SQLException e) {
					nlogger.log(e, Level.WARNING, "Failed to update record", json);
				}
			} else {
				nlogger.log(ex, Level.SEVERE, "Failed to insert record", json);
			}
		} finally {
			if (statement != null) {
				try {
					logger.finest("saveOrUpdate() - commiting changes.");
					connection.commit();
					statement.close();
					connection.close();
				} catch (SQLException e) {
					nlogger.log(e, Level.WARNING, "Failed to close connection.");
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tools.datasync.db2db.dao.GenericDao#saveOrUpdate(java.lang.String,
	 * java.util.List)
	 */
	public void saveOrUpdate(String entityName, List<JSON> jsonList, String keyColumn) {
		logger.finest("saveOrUpdate() - entityName=" + entityName + ", count=" + jsonList.size() + ", keyColumn=" + keyColumn);
		for (JSON json : jsonList) {
			this.saveOrUpdate(entityName, json, keyColumn);
		}
	}

}
