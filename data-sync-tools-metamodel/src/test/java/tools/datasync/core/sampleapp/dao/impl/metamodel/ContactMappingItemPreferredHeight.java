package tools.datasync.core.sampleapp.dao.impl.metamodel;

import tools.datasync.core.sampleapp.model.Contact;

public class ContactMappingItemPreferredHeight implements ContactValueMappingItem {

    public void mapValue(Contact contact, Object value) {
	if (value != null) {
	    contact.setPreferredHeight(value.toString());
	}
    }

}
