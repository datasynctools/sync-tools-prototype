package tools.datasync.dao.jdbc;

import tools.datasync.api.msg.SyncEntityMessage;
import tools.datasync.utils.SqlGenUtil;

public class UpdateSqlCreator implements SqlCreator {

    public String createSQL(String entityName, SyncEntityMessage json,
	    String keyColumn) {
	String sql = SqlGenUtil.getUpdateStatement(entityName, json, keyColumn);
	return sql;
    }

}
