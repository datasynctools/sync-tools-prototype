package tools.datasync.core.sampleapp.dao.impl.metamodel;

import tools.datasync.core.sampleapp.model.Contact;

public class ContactMappingItemLastName implements ContactValueMappingItem {

    public void mapValue(Contact contact, Object value) {
	if (value != null) {
	    contact.setLastName(value.toString());
	}
    }

}
