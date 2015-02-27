package tools.datasync.core.sampleapp.utils;

import java.sql.ResultSet;

public interface CreatorFromResultSet<T> {

    void createAndSetValue(ResultSet resultSet);

    T getCreatedValue();
}
