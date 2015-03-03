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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.apache.log4j.Logger;

import tools.datasync.basic.model.SyncEntityMessage;

public class SQLGenUtil {

    private static final Logger LOG = Logger.getLogger(SQLGenUtil.class
	    .getName());
    private static final DateFormat dateFormat = new SimpleDateFormat(
	    "yyyy-MM-dd hh:mm:ss");

    public static String getSelectSQL(String tableName) {
	StringBuffer sql = new StringBuffer();

	return sql.toString();
    }

    public static String getInsertStatement(String tableName, SyncEntityMessage json) {

	StringBuffer insert = new StringBuffer();
	insert.append("insert into ");
	insert.append(tableName);

	StringBuffer names = new StringBuffer();
	StringBuffer values = new StringBuffer();
	Iterator<String> keys = json.getData().keySet().iterator();

	handleInsertKeys(keys, json, names, values);

	insert.append(" (");
	insert.append(names);
	insert.append(") values (");
	insert.append(values);
	insert.append(")");

	LOG.debug(insert.toString());
	return insert.toString();
    }

    private static void handleInsertKeys(Iterator<String> keys, SyncEntityMessage json,
	    StringBuffer names, StringBuffer values) {
	while (keys.hasNext()) {
	    String name = keys.next();
	    if (StringUtils.isEmpty(json.get(name))
		    || "null".equals(json.get(name))) {
		continue;
	    }
	    names.append(name);
	    String type = json.getType(name);

	    handleType(new Tuple<String, String>(name, type), json, values);

	    appendNextIfAvailable(keys, names);
	    appendNextIfAvailable(keys, values);
	}
    }

    private static void handleType(Tuple<String, String> nameValue, SyncEntityMessage json,
	    StringBuffer values) {

	if ("String".equalsIgnoreCase(nameValue.y)) {
	    values.append("'");
	    values.append(json.get(nameValue.x));
	    values.append("'");
	} else if ("Date".equalsIgnoreCase(nameValue.y)
		|| "Long".equalsIgnoreCase(nameValue.y)) {
	    values.append("'");
	    values.append(dateFormat.format(new Date((Long) json
		    .get(nameValue.x))));
	    values.append("'");
	} else {
	    values.append(json.get(nameValue.x));
	}
    }

    private static void handleUpdateType(String name, Object value,
	    String type, StringBuffer sql) {

	sql.append(name);
	sql.append("=");
	if ("String".equalsIgnoreCase(type)) {
	    sql.append("'");
	    sql.append(value);
	    sql.append("'");
	} else if ("Date".equalsIgnoreCase(type)
		|| "Long".equalsIgnoreCase(type)) {
	    sql.append("'");
	    sql.append(dateFormat.format(new Date((Long) value)));
	    sql.append("'");
	} else {
	    sql.append(value);
	}

    }

    private static void appendNextIfAvailable(Iterator<String> keys,
	    StringBuffer buffer) {
	if (keys.hasNext()) {
	    buffer.append(", ");
	}
    }

    public static String getInsertPreparedStatement(String tableName, SyncEntityMessage json) {

	StringBuffer insertps = new StringBuffer();
	insertps.append("insert into ");
	insertps.append(tableName);

	StringBuffer names = new StringBuffer();
	StringBuffer values = new StringBuffer();
	Iterator<String> keys = json.getData().keySet().iterator();

	handleInsertPrepKeys(keys, json, names, values);

	insertps.append(" (");
	insertps.append(names);
	insertps.append(") values (");
	insertps.append(values);
	insertps.append(")");

	LOG.debug(insertps.toString());
	return insertps.toString();
    }

    private static void handleInsertPrepKeys(Iterator<String> keys, SyncEntityMessage json,
	    StringBuffer names, StringBuffer values) {
	while (keys.hasNext()) {
	    String name = keys.next();
	    names.append(name);
	    values.append('?');
	    if (keys.hasNext()) {
		names.append(", ");
		values.append(", ");
	    }
	}

    }

    public static String getUpdateStatement(String tableName, SyncEntityMessage json,
	    String keyColumn) {

	StringBuffer sql = new StringBuffer();
	sql.append("update ");
	sql.append(tableName);
	sql.append(" set ");

	Iterator<String> keys = json.getData().keySet().iterator();

	handleUpdateKeys(keys, json, sql);

	handleUpdateWhere(sql, keyColumn, json);

	LOG.debug(sql.toString());
	return sql.toString();
    }

    private static void handleUpdateWhere(StringBuffer sql, String keyColumn,
	    SyncEntityMessage json) {
	sql.append(" where ");
	String[] keyColumns = keyColumn.split(", ");
	for (int colIndex = 0; colIndex < keyColumns.length; colIndex++) {
	    sql.append(keyColumns[colIndex]);
	    sql.append("='");
	    sql.append(json.get(keyColumns[colIndex]));
	    sql.append("'");
	    if (colIndex + 1 < keyColumns.length) {
		sql.append(" and ");
	    }
	}

    }

    private static void handleUpdateKeys(Iterator<String> keys, SyncEntityMessage json,
	    StringBuffer sql) {
	while (keys.hasNext()) {
	    String name = keys.next();
	    Object value = json.get(name);
	    if (value == null || "null".equals(value) || "NULL".equals(value)) {
		continue;
	    }
	    String type = json.getType(name);

	    handleUpdateType(name, value, type, sql);

	    appendNextIfAvailable(keys, sql);
	}

    }

    public static String getDeleteStatement(String tableName, SyncEntityMessage json,
	    String keyColumn) {

	StringBuffer delete = new StringBuffer();
	delete.append("delete from ");
	delete.append(tableName);

	delete.append(" where ");
	delete.append(keyColumn);
	delete.append("='");
	delete.append(json.get(keyColumn));
	delete.append("'");

	LOG.debug(delete.toString());
	return delete.toString();
    }

    public static boolean isConstraintViolation(SQLException e) {
	return e.getSQLState().startsWith("23");
    }
}
