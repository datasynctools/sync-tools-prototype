package tools.datasync.core.sampleapp.dao.impl.metamodel;

import tools.datasync.core.sampleapp.model.Contact;

public interface ContactValueMappingItem {

    void mapValue(Contact contact, Object value);

}
