package tools.datasync.basic.dao;

import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Iterator;

import org.apache.log4j.Logger;

import tools.datasync.basic.model.JSON;

public class DbTableComparator {

	GenericDao sourceDao = null;
	GenericDao targetDao = null;

	private static final Logger logger = Logger.getLogger(DbTableComparator.class);

	public DbTableComparator(GenericDao sourceDao, GenericDao targetDao) {
		this.sourceDao = sourceDao;
		this.targetDao = targetDao;
		logger.info("Creating comparator... " + sourceDao + targetDao);
	}

	public void compare(String entityName) throws InputMismatchException, SQLException {

		logger.info(">>> Comparing tables: " + entityName);
		Iterator<JSON> sourceIterator = sourceDao.selectAll(entityName, true);
		Iterator<JSON> targetIterator = targetDao.selectAll(entityName, true);

		while (sourceIterator.hasNext() && targetIterator.hasNext()) {

			JSON source = sourceIterator.next();
			JSON target = targetIterator.next();

			if (source.equals(target)) {
				logger.info("Records match: " + source);
			} else {
				logger.info("**>> Records do not match: " + source + " ...and: " + target);
				throw new InputMismatchException("Records do not match for entity: " + entityName);
			}
		}
	}
}
