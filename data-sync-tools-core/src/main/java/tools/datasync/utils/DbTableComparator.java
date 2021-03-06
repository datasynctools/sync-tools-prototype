package tools.datasync.utils;

import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.api.dao.GenericDao;
import tools.datasync.api.msg.SyncEntityMessage;
import tools.datasync.dao.jdbc.GenericJdbcDao;

//TODO Is this better in a utility class or test class and not in core?
public class DbTableComparator {

    private GenericDao sourceDao = null;
    private GenericDao targetDao = null;

    private static final Logger LOG = LoggerFactory
	    .getLogger(GenericJdbcDao.class);

    public DbTableComparator(GenericDao sourceDao, GenericDao targetDao) {
	this.sourceDao = sourceDao;
	this.targetDao = targetDao;
	LOG.info("Creating comparator... " + sourceDao + targetDao);
    }

    private Map<String, SyncEntityMessage> mapResults(
	    Iterator<SyncEntityMessage> jsonIterator) {
	Map<String, SyncEntityMessage> answer = new HashMap<String, SyncEntityMessage>();
	while (jsonIterator.hasNext()) {
	    SyncEntityMessage json = jsonIterator.next();
	    answer.put(json.getCalculatedPrimaryKey(), json);
	}
	return (answer);
    }

    public void compare(String entityName) throws InputMismatchException,
	    Exception {

	LOG.info(">>> Comparing tables: " + entityName);
	Map<String, SyncEntityMessage> sourceMap = mapResults(sourceDao
		.selectAll(entityName, true));
	Map<String, SyncEntityMessage> targetMap = mapResults(targetDao
		.selectAll(entityName, true));

	Iterator<String> sourceKeys = sourceMap.keySet().iterator();

	compare(sourceKeys, sourceMap, targetMap, entityName);

	if (targetMap.size() > 0) {
	    String msg = "Target for entity "
		    + entityName
		    + " has extra records that are not found in source. records="
		    + targetMap;
	    LOG.error("**>> " + msg);
	    throw new InputMismatchException(msg);
	}
    }

    private void compare(Iterator<String> sourceKeys,
	    Map<String, SyncEntityMessage> sourceMap,
	    Map<String, SyncEntityMessage> targetMap, String entityName) {
	while (sourceKeys.hasNext()) {
	    String sourceKey = sourceKeys.next();
	    SyncEntityMessage source = sourceMap.get(sourceKey);
	    if (targetMap.containsKey(sourceKey)) {
		SyncEntityMessage target = targetMap.get(sourceKey);
		if (source.equals(target)) {
		    LOG.info("Records match in entity {} and record {}",
			    entityName, sourceKey);
		    // Remove the value from the target map (will use later to
		    // make sure there aren't missing values in source)
		    targetMap.remove(sourceKey);
		} else {
		    String msg = "Records do not match in entity " + entityName
			    + " record " + sourceKey + ", \nsource=" + source
			    + "\ntarget=" + target;
		    LOG.error("**>> " + msg);
		    throw new InputMismatchException(msg);
		}
	    } else {
		String msg = "Source Record in entity " + entityName
			+ " found in source but not target for record "
			+ sourceKey + ", source json=" + source;
		LOG.error("**>> " + msg);
		throw new InputMismatchException(msg);
	    }
	}
    }
}
