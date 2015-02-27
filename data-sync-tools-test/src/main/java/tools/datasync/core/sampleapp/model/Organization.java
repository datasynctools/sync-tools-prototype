package tools.datasync.core.sampleapp.model;

public class Organization {
    private String organizationId; // OrganizationId varchar(36) not null,
    private String name; // Name varchar(50) not null,

    // primary key (OrganizationId)
    public String getOrganizationId() {
	return organizationId;
    }

    public void setOrganizationId(String organizationId) {
	this.organizationId = organizationId;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

}
