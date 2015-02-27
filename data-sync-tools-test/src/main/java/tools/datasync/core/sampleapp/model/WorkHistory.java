package tools.datasync.core.sampleapp.model;

public class WorkHistory {
    private String workHistoryId; // WorkHistoryId varchar(36) not null,
    private String contactId; // ContactId varchar(36) not null,
    private String organizationId; // OrganizationId varchar(36),
    private String startMonth;// StartMonth varchar(2),
    private String startYear; // StartYear varchar(4),
    private String endMonth; // EndMonth varchar(2),
    private String endYear;// EndYear varchar(4),
    private String jobDescription; // JobDescription varchar(200),
    private String jobTitle;// JobTitle varchar(50),

    // primary key (WorkHistoryId),
    // FOREIGN KEY (ContactId) REFERENCES org.Contact(ContactId),
    // FOREIGN KEY (OrganizationId) REFERENCES org.Organization(OrganizationId)
    public String getWorkHistoryId() {
	return workHistoryId;
    }

    public void setWorkHistoryId(String workHistoryId) {
	this.workHistoryId = workHistoryId;
    }

    public String getContactId() {
	return contactId;
    }

    public void setContactId(String contactId) {
	this.contactId = contactId;
    }

    public String getOrganizationId() {
	return organizationId;
    }

    public void setOrganizationId(String organizationId) {
	this.organizationId = organizationId;
    }

    public String getStartMonth() {
	return startMonth;
    }

    public void setStartMonth(String startMonth) {
	this.startMonth = startMonth;
    }

    public String getStartYear() {
	return startYear;
    }

    public void setStartYear(String startYear) {
	this.startYear = startYear;
    }

    public String getEndMonth() {
	return endMonth;
    }

    public void setEndMonth(String endMonth) {
	this.endMonth = endMonth;
    }

    public String getEndYear() {
	return endYear;
    }

    public void setEndYear(String endYear) {
	this.endYear = endYear;
    }

    public String getJobDescription() {
	return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
	this.jobDescription = jobDescription;
    }

    public String getJobTitle() {
	return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
	this.jobTitle = jobTitle;
    }

}
