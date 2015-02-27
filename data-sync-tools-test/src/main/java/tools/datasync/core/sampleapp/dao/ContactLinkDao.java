package tools.datasync.core.sampleapp.dao;

import java.util.List;

import tools.datasync.core.sampleapp.model.ContactLink;
import tools.datasync.core.sampleapp.model.WorkHistory;

public interface ContactLinkDao {

    void addContactLink(ContactLink contactLink);

    void removeContactLink(ContactLink contactLink);

    List<ContactLink> getContactLinksByContactId(String contactId);

    void changeWorkHistoryId(WorkHistory previousItem, String newWorkHistoryId);

}
