package tools.datasync.dao.jdbc;

import tools.datasync.basic.model.SyncEntityMessage;

public interface SqlCreator {
    String createSQL(String entityName, SyncEntityMessage json, String keyColumn);
}
