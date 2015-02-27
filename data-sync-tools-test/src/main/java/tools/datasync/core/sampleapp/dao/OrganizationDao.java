package tools.datasync.core.sampleapp.dao;

import java.util.List;

import tools.datasync.core.sampleapp.model.Organization;

public interface OrganizationDao {

    void addOrganization(Organization item);

    void removeOrganization(String id);

    List<Organization> getOrganizations();

    List<Organization> getOrganizationByContactId(String contactId);

    void updateOrganization(Organization item);

}
