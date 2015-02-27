package tools.datasync.core.sampleapp.dao.impl.metamodel;

import tools.datasync.core.sampleapp.model.Contact;

public class ContactMappingItemHeightFt implements ContactValueMappingItem {

    public void mapValue(Contact contact, Object value) {
	contact.setHeightFt((Integer) value);
    }

}
