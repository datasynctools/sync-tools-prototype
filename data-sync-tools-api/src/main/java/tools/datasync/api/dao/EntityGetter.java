package tools.datasync.api.dao;

public interface EntityGetter {
    String getId(String entityName);

    String getName(String id);

    String getSyncStateName();
}
