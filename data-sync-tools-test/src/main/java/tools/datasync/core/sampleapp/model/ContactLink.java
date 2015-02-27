package tools.datasync.core.sampleapp.model;

public class ContactLink {

    private String sourceContactId;// SourceContactId varchar(36) not null,
    private String targetContactId;// TargetContactId varchar(36) not null,
    private String workHistoryId; // WorkHistoryId varchar(36) not null,

    // primary key (SourceContactId, TargetContactId, WorkHistoryId),
    // FOREIGN KEY (SourceContactId) REFERENCES org.Contact(ContactId),
    // FOREIGN KEY (TargetContactId) REFERENCES org.Contact(ContactId),
    // FOREIGN KEY (WorkHistoryId) REFERENCES org.WorkHistory(WorkHistoryId)

    public String getSourceContactId() {
	return sourceContactId;
    }

    public void setSourceContactId(String sourceContactId) {
	this.sourceContactId = sourceContactId;
    }

    public String getTargetContactId() {
	return targetContactId;
    }

    public void setTargetContactId(String targetContactId) {
	this.targetContactId = targetContactId;
    }

    public String getWorkHistoryId() {
	return workHistoryId;
    }

    public void setWorkHistoryId(String workHistoryId) {
	this.workHistoryId = workHistoryId;
    }

}
