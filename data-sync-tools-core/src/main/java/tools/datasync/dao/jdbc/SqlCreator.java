package tools.datasync.dao.jdbc;

import tools.datasync.api.msg.SyncEntityMessage;

public interface SqlCreator {
    String createSQL(String entityName, SyncEntityMessage syncEntityMsg, String keyColumn);
}
