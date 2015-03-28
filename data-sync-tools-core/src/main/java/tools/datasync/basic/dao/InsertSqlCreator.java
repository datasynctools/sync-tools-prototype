package tools.datasync.basic.dao;

import tools.datasync.basic.model.SyncEntityMessage;
import tools.datasync.utils.SqlGenUtil;

public class InsertSqlCreator implements SqlCreator {

    public String createSQL(String entityName, SyncEntityMessage json,
	    String keyColumn) {
	String sql = SqlGenUtil.getInsertStatement(entityName, json);
	return sql;
    }

}
