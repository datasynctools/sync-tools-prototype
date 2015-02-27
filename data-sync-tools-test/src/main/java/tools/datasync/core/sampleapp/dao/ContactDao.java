package tools.datasync.core.sampleapp.dao;

import java.util.List;

import tools.datasync.core.sampleapp.model.Contact;

public interface ContactDao {

    void addContact(Contact item);

    void removeContact(String id);

    List<Contact> getContacts();

    void updateContact(Contact item);

}
