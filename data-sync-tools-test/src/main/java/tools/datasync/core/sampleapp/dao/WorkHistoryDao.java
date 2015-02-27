package tools.datasync.core.sampleapp.dao;

import java.util.List;

import tools.datasync.core.sampleapp.model.WorkHistory;

public interface WorkHistoryDao {

    void addWorkHistory(WorkHistory item);

    void removeWorkHistory(String id);

    List<WorkHistory> getWorkHistories();

    void updateWorkHistory(WorkHistory item);

    void changeOrganizationId(WorkHistory previousItem, String newOrgId);

}
