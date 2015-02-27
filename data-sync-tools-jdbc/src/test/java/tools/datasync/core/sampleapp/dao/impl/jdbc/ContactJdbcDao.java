package tools.datasync.core.sampleapp.dao.impl.jdbc;

import static tools.datasync.core.sampleapp.dao.impl.jdbc.ContactInsertHelper.prepareInsert;
import static tools.datasync.core.sampleapp.dao.impl.jdbc.ContactUpdateHelper.prepareUpdate;
import static tools.datasync.core.sampleapp.utils.CommonJdbcHelper.processSqlMutation;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import tools.datasync.api.dao.SyncRecord;
import tools.datasync.api.dao.SyncRecordFromT;
import tools.datasync.api.impl.jdbc.JdbcSyncRecordMutationBuilder;
import tools.datasync.core.sampleapp.dao.ContactDao;
import tools.datasync.core.sampleapp.model.Contact;
import tools.datasync.core.sampleapp.utils.CommonJdbcHelper;
import tools.datasync.core.sampleapp.utils.CreatorFromResultSet;

public class ContactJdbcDao implements ContactDao {

    private DataSource dataSource;
    private JdbcSyncRecordMutationBuilder syncRecordMutator = new JdbcSyncRecordMutationBuilder();
    private SyncRecordFromT<Contact> syncRecordCreator;

    private String contactTableName = "org.Contact";

    public ContactJdbcDao(DataSource dataSource,
	    SyncRecordFromT<Contact> syncRecordCreator) {
	this.dataSource = dataSource;
	this.syncRecordCreator = syncRecordCreator;
    }

    public void addContact(Contact item) {

	SyncRecord syncRecord = syncRecordCreator.create(item);
	List<String> sqlList = syncRecordMutator.createInsertSyncData(null,
		syncRecord);

	String sql = prepareInsert(item, contactTableName);

	sqlList.add(sql);

	processSqlMutation(dataSource, sqlList);

    }

    public void removeContact(String id) {

	List<String> sqlList = new ArrayList<String>(0);
	// TODO Add SyncData hook to handle delete

	String sql = "delete from Contact where CustomerId='" + id + "'";
	sqlList.add(sql);

	processSqlMutation(dataSource, sqlList);

    }

    public List<Contact> getContacts() {
	String sql = ContactSelectHelper.prepareSelectAll(contactTableName);
	CreatorFromResultSet<List<Contact>> contactCreator = new ContactCreatorFromResultSet();
	CommonJdbcHelper.processSqlSelect(dataSource, sql, contactCreator);
	List<Contact> answer = contactCreator.getCreatedValue();
	return answer;
    }

    public void updateContact(Contact item) {

	SyncRecord syncRecord = syncRecordCreator.create(item);
	List<String> sqlList = syncRecordMutator.createUpdateSyncData(null,
		syncRecord);

	String sql = prepareUpdate(item);

	sqlList.add(sql);

	processSqlMutation(dataSource, sqlList);

    }

    public void setSyncRecordMutator(
	    JdbcSyncRecordMutationBuilder syncRecordMutator) {
	this.syncRecordMutator = syncRecordMutator;
    }

    public String getContactTableName() {
	return contactTableName;
    }

    public void setContactTableName(String contactTableName) {
	this.contactTableName = contactTableName;
    }

}
