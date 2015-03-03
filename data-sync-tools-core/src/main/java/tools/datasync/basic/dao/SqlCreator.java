package tools.datasync.basic.dao;

import tools.datasync.basic.model.SyncEntityMessage;

public interface SqlCreator {
    String createSQL(String entityName, SyncEntityMessage json, String keyColumn);
}
