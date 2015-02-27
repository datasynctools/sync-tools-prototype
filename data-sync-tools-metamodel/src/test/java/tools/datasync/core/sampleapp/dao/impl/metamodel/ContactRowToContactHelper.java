package tools.datasync.core.sampleapp.dao.impl.metamodel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.metamodel.UpdateableDataContext;
import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.data.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.core.sampleapp.model.Contact;

public class ContactRowToContactHelper {

    public final static int ORDER_CONTACT_ID = 0;

    private final static Logger LOG = LoggerFactory
	    .getLogger(ContactMetamodelDao.class);

    public final static DataSet doQuery(UpdateableDataContext dataContext) {
	DataSet dataSet = dataContext.query().from("Contact")
		.select("ContactId").select("DateOfBirth").select("FirstName")
		.select("LastName").select("HeightFt").select("HeightInch")
		.select("Picture").select("PreferredHeight").execute();
	return (dataSet);
    }

    private static final Map<Integer, ContactValueMappingItem> getContactValueMappingChain() {
	Map<Integer, ContactValueMappingItem> contactValueMappingMap = new HashMap<Integer, ContactValueMappingItem>();

	contactValueMappingMap.put(0, new ContactMappingItemContactId());
	contactValueMappingMap.put(1, new ContactMappingItemDateOfBirth());
	contactValueMappingMap.put(2, new ContactMappingItemFirstName());
	contactValueMappingMap.put(3, new ContactMappingItemLastName());
	contactValueMappingMap.put(4, new ContactMappingItemHeightFt());
	contactValueMappingMap.put(5, new ContactMappingItemHeightInch());
	contactValueMappingMap.put(6, new ContactMappingItemPicture());
	contactValueMappingMap.put(7, new ContactMappingItemPreferredHeight());

	return (contactValueMappingMap);
    }

    public static final void addContactToList(Row row, List<Contact> contacts) {

	int counter = 0;

	Map<Integer, ContactValueMappingItem> contactValueMappingMap = getContactValueMappingChain();

	Contact contact = new Contact();

	for (Object value : row.getValues()) {

	    ContactValueMappingItem mapper = contactValueMappingMap
		    .get(counter);
	    if (mapper != null) {
		mapper.mapValue(contact, value);
	    } else {
		LOG.warn("No Mapper for column number {}", counter);
	    }
	    counter++;
	}

	contacts.add(contact);

    }
}
