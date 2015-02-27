package tools.datasync.api.impl.jdbc;

import static tools.datasync.api.impl.jdbc.SyncRecordDeleteHelper.prepareDelete;
import static tools.datasync.api.impl.jdbc.SyncRecordInsertHelper.prepareInsert;
import static tools.datasync.api.impl.jdbc.SyncRecordUpdateHelper.prepareUpdate;

import java.util.ArrayList;
import java.util.List;

import tools.datasync.api.dao.SyncRecord;
import tools.datasync.api.dao.SyncRecordMutationBuilder;

public class JdbcSyncRecordMutationBuilder
	implements
	SyncRecordMutationBuilder<Void, List<String>, List<String>, List<String>> {

    private String syncStateTableName = "seed.SyncState";

    public List<String> createInsertSyncData(Void empty,
	    final SyncRecord syncRecord) {

	List<String> answer = new ArrayList<String>();

	String sql = prepareInsert(syncRecord, syncStateTableName);

	answer.add(sql);

	return answer;
    }

    public List<String> createUpdateSyncData(Void empty,
	    final SyncRecord syncRecord) {

	List<String> answer = new ArrayList<String>();

	String sql = prepareUpdate(syncRecord);

	answer.add(sql);

	return answer;
    }

    public String getSyncStateTableName() {
	return syncStateTableName;
    }

    public void setSyncStateTableName(String syncStateTableName) {
	this.syncStateTableName = syncStateTableName;
    }

    public List<String> createDeleteSyncData(Void context, SyncRecord syncRecord) {
	List<String> answer = new ArrayList<String>();

	String sql = prepareDelete(syncRecord);

	answer.add(sql);

	return answer;
    }

}
