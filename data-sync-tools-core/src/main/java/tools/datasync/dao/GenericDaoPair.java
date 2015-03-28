package tools.datasync.dao;

import tools.datasync.api.dao.GenericDao;

public class GenericDaoPair {

    private GenericDao sourceDao;
    private GenericDao targetDao;

    public GenericDaoPair(GenericDao sourceDao, GenericDao targetDao) {
	this.sourceDao = sourceDao;
	this.targetDao = targetDao;
    }

    public GenericDao getSourceDao() {
	return sourceDao;
    }

    public GenericDao getTargetDao() {
	return targetDao;
    }

}
