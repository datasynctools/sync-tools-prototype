package tools.datasync.basic.model;

public interface EntityGetter {
    String getId(String entityName);

    String getName(String id);

    String getSyncStateName();
}
