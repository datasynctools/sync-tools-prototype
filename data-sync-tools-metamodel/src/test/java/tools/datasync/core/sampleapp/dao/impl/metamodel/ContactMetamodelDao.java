package tools.datasync.core.sampleapp.dao.impl.metamodel;

import static tools.datasync.core.sampleapp.dao.impl.metamodel.ContactRowToContactHelper.addContactToList;
import static tools.datasync.core.sampleapp.dao.impl.metamodel.ContactRowToContactHelper.doQuery;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.metamodel.UpdateCallback;
import org.apache.metamodel.UpdateScript;
import org.apache.metamodel.UpdateableDataContext;
import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.data.Row;
import org.apache.metamodel.insert.RowInsertionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.api.dao.SyncRecord;
import tools.datasync.api.dao.SyncRecordFromT;
import tools.datasync.api.impl.metamodel.MetadataSyncRecordMutationBuilder;
import tools.datasync.core.sampleapp.dao.ContactDao;
import tools.datasync.core.sampleapp.model.Contact;

public class ContactMetamodelDao implements ContactDao {

    private final static Logger LOG = LoggerFactory
	    .getLogger(ContactMetamodelDao.class);

    private UpdateableDataContext dataContext;
    private MetadataSyncRecordMutationBuilder syncRecordMutator = new MetadataSyncRecordMutationBuilder();
    private SyncRecordFromT<Contact> syncRecordCreator;

    public ContactMetamodelDao(UpdateableDataContext dataContext,
	    SyncRecordFromT<Contact> syncRecordCreator) {
	this.dataContext = dataContext;
	this.syncRecordCreator = syncRecordCreator;
    }

    public void addContact(final Contact item) {

	final SyncRecord syncRecord = syncRecordCreator.create(item);

	UpdateScript updateScript = new UpdateScript() {
	    public void run(UpdateCallback callback) {

		syncRecordMutator.createInsertSyncData(callback, syncRecord)
			.execute();

		createInsertForContact(callback, item).execute();
	    }
	};

	dataContext.executeUpdate(updateScript);

    }

    private RowInsertionBuilder createInsertForContact(UpdateCallback callback,
	    Contact item) {
	RowInsertionBuilder answer = callback.insertInto("Contact")
		.value("ContactId", item.getContactId())
		.value("DateOfBirth", item.getDateOfBirth())
		.value("FirstName", item.getFirstName())
		.value("LastName", item.getLastName())
		.value("HeightFt", item.getHeightFt())
		.value("HeightInch", item.getHeightInch())
		.value("Picture", item.getPicture())
		.value("PreferredHeight", item.getPreferredHeight());
	return (answer);
    }

    public void removeContact(final String id) {

	UpdateScript updateScript = new UpdateScript() {
	    public void run(UpdateCallback callback) {
		callback.deleteFrom("Contact").where("ContactId").eq(id)
			.execute();
	    }
	};
	dataContext.executeUpdate(updateScript);

    }

    public List<Contact> getContacts() {

	DataSet dataSet = doQuery(dataContext);

	List<Contact> contacts = new ArrayList<Contact>();

	if (dataSet != null) {
	    Iterator<Row> iterator = dataSet.iterator();
	    while (iterator.hasNext()) {
		Row row = iterator.next();
		addContactToList(row, contacts);
	    }
	} else {
	    LOG.debug("Dataset is null, no records found");
	}
	return (contacts);

    }

    public void updateContact(final Contact item) {
	UpdateScript updateScript = new UpdateScript() {
	    public void run(UpdateCallback callback) {
		callback.update("Contact")
			.value("ContactId", item.getContactId())
			.value("DateOfBirth", item.getDateOfBirth())
			.value("FirstName", item.getFirstName())
			.value("LastName", item.getLastName())
			.value("HeightFt", item.getHeightFt())
			.value("HeightInch", item.getHeightInch())
			.value("Picture", item.getPicture())
			.value("PreferredHeight", item.getPreferredHeight())
			.execute();
	    }
	};
	dataContext.executeUpdate(updateScript);
    }

    public void setSyncRecordMutator(
	    MetadataSyncRecordMutationBuilder syncRecordMutator) {
	this.syncRecordMutator = syncRecordMutator;
    }

}
