package tools.datasync.basic.dao;

import tools.datasync.basic.model.JSON;

public interface SqlCreator {
    String createSQL(String entityName, JSON json, String keyColumn);
}
