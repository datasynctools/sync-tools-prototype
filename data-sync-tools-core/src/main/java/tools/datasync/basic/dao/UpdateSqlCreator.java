package tools.datasync.basic.dao;

import tools.datasync.basic.model.JSON;
import tools.datasync.basic.util.SQLGenUtil;

public class UpdateSqlCreator implements SqlCreator {

    public String createSQL(String entityName, JSON json, String keyColumn) {
	String sql = SQLGenUtil.getUpdateStatement(entityName, json, keyColumn);
	return sql;
    }

}
