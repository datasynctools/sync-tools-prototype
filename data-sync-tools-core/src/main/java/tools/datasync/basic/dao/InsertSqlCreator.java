package tools.datasync.basic.dao;

import tools.datasync.basic.model.SyncEntityMessage;
import tools.datasync.basic.util.SQLGenUtil;

public class InsertSqlCreator implements SqlCreator {

    public String createSQL(String entityName, SyncEntityMessage json, String keyColumn) {
	String sql = SQLGenUtil.getInsertStatement(entityName, json);
	return sql;
    }

}
