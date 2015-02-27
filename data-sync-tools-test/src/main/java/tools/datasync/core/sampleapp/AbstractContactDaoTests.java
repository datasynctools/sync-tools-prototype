package tools.datasync.core.sampleapp;

import java.util.List;
import java.util.UUID;

import junit.framework.Assert;

import org.junit.Test;

import tools.datasync.core.sampleapp.dao.ContactDao;
import tools.datasync.core.sampleapp.model.Contact;

public abstract class AbstractContactDaoTests {

    public abstract ContactDao setupTestInsertAndSelect();

    @Test
    public void testInsertAndSelect() {

	ContactDao contactDao = setupTestInsertAndSelect();

	String contactId = UUID.randomUUID().toString();
	Contact item = createTestContact(contactId);

	contactDao.addContact(item);

	List<Contact> contacts = contactDao.getContacts();
	Assert.assertEquals(1, contactDao.getContacts().size());
	Contact result = contacts.get(0);

	verifyContactInsertAndSelect(result, contacts, contactId);

	// SyncStateDao syncStateDao = null;

    }

    private Contact createTestContact(String contactId) {
	Contact item = new Contact();
	item.setContactId(contactId);
	item.setFirstName("John");
	item.setLastName("Doe");
	item.setHeightInch(9);
	item.setHeightFt(5);
	return (item);
    }

    private void verifyContactInsertAndSelect(Contact result,
	    List<Contact> contacts, String contactId) {

	Assert.assertEquals(1, contacts.size());
	Assert.assertEquals(contactId, result.getContactId());
	Assert.assertEquals("John", result.getFirstName());
	Assert.assertEquals("Doe", result.getLastName());
	Assert.assertEquals(5, result.getHeightFt());
	Assert.assertEquals(9, result.getHeightInch());
    }
}
