package tools.datasync.basic.logic;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import tools.datasync.basic.dao.GenericDao;
import tools.datasync.basic.model.JSON;

public class DbTableComparator {

    private GenericDao sourceDao = null;
    private GenericDao targetDao = null;

    private static final Logger logger = Logger
	    .getLogger(DbTableComparator.class);

    public DbTableComparator(GenericDao sourceDao, GenericDao targetDao) {
	this.sourceDao = sourceDao;
	this.targetDao = targetDao;
	logger.info("Creating comparator... " + sourceDao + targetDao);
    }

    private Map<String, JSON> mapResults(Iterator<JSON> jsonIterator) {
	Map<String, JSON> answer = new HashMap<String, JSON>();
	while (jsonIterator.hasNext()) {
	    JSON json = jsonIterator.next();
	    answer.put(json.getCalculatedPrimaryKey(), json);
	}
	return (answer);
    }

    public void compare(String entityName) throws InputMismatchException,
	    SQLException {

	logger.info(">>> Comparing tables: " + entityName);
	Map<String, JSON> sourceMap = mapResults(sourceDao.selectAll(
		entityName, true));
	Map<String, JSON> targetMap = mapResults(targetDao.selectAll(
		entityName, true));

	Iterator<String> sourceKeys = sourceMap.keySet().iterator();

	compare(sourceKeys, sourceMap, targetMap, entityName);

	if (targetMap.size() > 0) {
	    String msg = "Target for entity "
		    + entityName
		    + " has extra records that are not found in source. records="
		    + targetMap;
	    logger.error("**>> " + msg);
	    throw new InputMismatchException(msg);
	}
    }

    private void compare(Iterator<String> sourceKeys,
	    Map<String, JSON> sourceMap, Map<String, JSON> targetMap,
	    String entityName) {
	while (sourceKeys.hasNext()) {
	    String sourceKey = sourceKeys.next();
	    JSON source = sourceMap.get(sourceKey);
	    if (targetMap.containsKey(sourceKey)) {
		JSON target = targetMap.get(sourceKey);
		if (source.equals(target)) {
		    logger.info("Records match in entity " + entityName
			    + " and record " + sourceKey + ", json=" + source);
		    // Remove the value from the target map (will use later to
		    // make sure there aren't missing values in source)
		    targetMap.remove(sourceKey);
		} else {
		    String msg = "Records do not match in entity " + entityName
			    + " record " + sourceKey + ", \nsource=" + source
			    + "\ntarget=" + target;
		    logger.error("**>> " + msg);
		    throw new InputMismatchException(msg);
		}
	    } else {
		String msg = "Source Record in entity " + entityName
			+ " found in source but not target for record "
			+ sourceKey + ", source json=" + source;
		logger.error("**>> " + msg);
		throw new InputMismatchException(msg);
	    }
	}
    }
}
