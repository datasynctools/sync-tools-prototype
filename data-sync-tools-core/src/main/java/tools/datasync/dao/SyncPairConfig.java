package tools.datasync.dao;

import tools.datasync.basic.logic.ConflictResolverFactory;
import tools.datasync.basic.seed.SeedConsumerFactory;

public class SyncPairConfig {

    private GenericDao sourceDao;
    private GenericDao targetDao;
    private SeedConsumerFactory seedConsumerFactory;
    private ConflictResolverFactory conflictResolverFactory;

    public SyncPairConfig(GenericDao sourceDao, GenericDao targetDao,
	    SeedConsumerFactory seedConsumerFactory,
	    ConflictResolverFactory conflictResolverFactory) {
	this.sourceDao = sourceDao;
	this.targetDao = targetDao;
	this.seedConsumerFactory = seedConsumerFactory;
	this.conflictResolverFactory = conflictResolverFactory;
    }

    public GenericDao getSourceDao() {
	return sourceDao;
    }

    public GenericDao getTargetDao() {
	return targetDao;
    }

    public SeedConsumerFactory getSeedConsumerFactory() {
	return seedConsumerFactory;
    }

    public ConflictResolverFactory getConflictResolverFactory() {
	return conflictResolverFactory;
    }

}
