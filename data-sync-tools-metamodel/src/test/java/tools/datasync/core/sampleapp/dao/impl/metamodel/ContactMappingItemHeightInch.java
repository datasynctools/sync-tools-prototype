package tools.datasync.core.sampleapp.dao.impl.metamodel;

import tools.datasync.core.sampleapp.model.Contact;

public class ContactMappingItemHeightInch implements ContactValueMappingItem {

    public void mapValue(Contact contact, Object value) {
	contact.setHeightInch((Integer) value);
    }

}
