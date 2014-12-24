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

package tools.datasync.basic.util;

import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import tools.datasync.basic.model.JSON;

public class SQLGenUtil {

	private static Logger logger = Logger.getLogger(SQLGenUtil.class.getName());
	
	public static String getSelectSQL(String tableName) {
		StringBuffer sql = new StringBuffer();

		return sql.toString();
	}

	public static String getInsertStatement(String tableName, JSON json) {

		StringBuffer insert = new StringBuffer();
		insert.append("insert into ");
		insert.append(tableName);

		StringBuffer names = new StringBuffer();
		StringBuffer values = new StringBuffer();
		Iterator<String> keys = json.getData().keySet().iterator();
		while (keys.hasNext()) {
			String name = keys.next();
			if(StringUtils.isEmpty(json.get(name))
			        || "null".equals(json.get(name))){
			    continue;
			}
			names.append(name);
			String type = json.getType(name);
			if("String".equalsIgnoreCase(type)){
			    values.append("'");
	            values.append(json.get(name));
	            values.append("'");
			} else if("Date".equalsIgnoreCase(type) || "Long".equalsIgnoreCase(type)){
                values.append("'");
                values.append(new Date((Long)json.get(name)));
                values.append("'");
            } else {
			    values.append(json.get(name));
			}
			
			if (keys.hasNext()) {
				names.append(", ");
				values.append(", ");
			}
		}
		insert.append(" (");
		insert.append(names);
		insert.append(") values (");
		insert.append(values);
		insert.append(")");

		logger.log(Level.ALL, insert.toString());
		return insert.toString();
	}
	
	public static String getInsertPreparedStatement(String tableName, JSON json) {

		StringBuffer insertps = new StringBuffer();
		insertps.append("insert into ");
		insertps.append(tableName);

		StringBuffer names = new StringBuffer();
		StringBuffer values = new StringBuffer();
		Iterator<String> keys = json.getData().keySet().iterator();
		while (keys.hasNext()) {
			String name = keys.next();
			names.append(name);
			values.append('?');
			if (keys.hasNext()) {
				names.append(", ");
				values.append(", ");
			}
		}
		insertps.append(" (");
		insertps.append(names);
		insertps.append(") values (");
		insertps.append(values);
		insertps.append(")");

		logger.log(Level.ALL, insertps.toString());
		return insertps.toString();
	}

	public static String getUpdateStatement(String tableName, JSON json, String keyColumn) {

		StringBuffer update = new StringBuffer();
		update.append("update ");
		update.append(tableName);
		update.append(" set ");

		Iterator<String> keys = json.getData().keySet().iterator();
		while (keys.hasNext()) {
			String name = keys.next();
			Object value = json.get(name);
			if(value == null || "null".equals(value) || "NULL".equals(value)){
				continue;
			}
			update.append(name);
			update.append("='");
			update.append(value);
			update.append("' ");
			if (keys.hasNext()) {
				update.append(", ");
			}
		}
		
		update.append(" where ");
		update.append(keyColumn);
		update.append("='");
		update.append(json.get(keyColumn));
		update.append("'");

		logger.log(Level.ALL, update.toString());
		return update.toString();
	}
	
	public static String getDeleteStatement(String tableName, JSON json, String keyColumn) {

        StringBuffer delete = new StringBuffer();
        delete.append("delete from ");
        delete.append(tableName);
        
        delete.append(" where ");
        delete.append(keyColumn);
        delete.append("='");
        delete.append(json.get(keyColumn));
        delete.append("'");

        logger.log(Level.ALL, delete.toString());
        return delete.toString();
    }
	
	public static boolean isConstraintViolation(SQLException e) {
	    return e.getSQLState().startsWith("23");
	}
}
