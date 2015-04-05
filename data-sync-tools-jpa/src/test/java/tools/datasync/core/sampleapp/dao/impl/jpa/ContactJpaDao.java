package tools.datasync.core.sampleapp.dao.impl.jpa;

import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.metamodel.EntityType;

import tools.datasync.api.dao.SyncRecord;
import tools.datasync.api.dao.SyncRecordFromT;
import tools.datasync.api.impl.jpa.JpaSyncRecord;
import tools.datasync.api.impl.jpa.JpaSyncRecordMutationBuilder;
import tools.datasync.core.sampleapp.dao.ContactDao;
import tools.datasync.core.sampleapp.model.Contact;

public class ContactJpaDao implements ContactDao {

    private EntityManagerFactory entityFactory;
    private JpaSyncRecordMutationBuilder syncRecordMutator = new JpaSyncRecordMutationBuilder();
    private SyncRecordFromT<Contact> syncRecordCreator;

    public ContactJpaDao(EntityManagerFactory entityFactory,
	    SyncRecordFromT<Contact> syncRecordCreator) {
	this.entityFactory = entityFactory;
	this.syncRecordCreator = syncRecordCreator;
    }

    public void addContact(Contact item) {

	SyncRecord syncRecord = syncRecordCreator.create(item);
	JpaSyncRecord jpaSyncRecord = syncRecordMutator.createInsertSyncData(
		null, syncRecord);

	EntityManager entityManager = entityFactory.createEntityManager();
	entityManager.getTransaction().begin();

	entityManager.persist(item);
	entityManager.persist(jpaSyncRecord);

	entityManager.getTransaction().commit();
	entityManager.close();
    }

    public void removeContact(String id) {

	// TODO Add SyncData hook to handle delete

    }

    public List<Contact> getContacts() {

	EntityManager entityManager = entityFactory.createEntityManager();

	Iterator<EntityType<?>> iterator = entityManager.getMetamodel()
		.getEntities().iterator();
	while (iterator.hasNext()) {
	    EntityType<?> type = iterator.next();
	    System.out.println(type.getName());

	}
	Query query = entityManager.createQuery("SELECT e FROM Contact e",
		Contact.class);
	@SuppressWarnings("unchecked")
	List<Contact> contactList = query.getResultList();

	entityManager.close();

	return contactList;
    }

    public void updateContact(Contact item) {

	SyncRecord syncRecord = syncRecordCreator.create(item);
	JpaSyncRecord jpaSyncRecord = syncRecordMutator.createInsertSyncData(
		null, syncRecord);

	EntityManager entityManager = entityFactory.createEntityManager();
	entityManager.getTransaction().begin();

	entityManager.persist(item);
	entityManager.persist(jpaSyncRecord);

	entityManager.getTransaction().commit();
	entityManager.close();
    }

    public void setSyncRecordMutator(
	    JpaSyncRecordMutationBuilder syncRecordMutator) {
	this.syncRecordMutator = syncRecordMutator;
    }

}
