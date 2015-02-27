package tools.datasync.core.sampleapp.dao.impl.metamodel;

import java.util.Date;

import tools.datasync.core.sampleapp.model.Contact;

public class ContactMappingItemDateOfBirth implements ContactValueMappingItem {

    public void mapValue(Contact contact, Object value) {
	if (value != null) {
	    contact.setDateOfBirth((Date) value);
	}
    }

}
