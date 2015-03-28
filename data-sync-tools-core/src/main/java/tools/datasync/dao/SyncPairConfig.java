package tools.datasync.dao;

import tools.datasync.api.conflict.ConflictResolverFactory;
import tools.datasync.api.dao.GenericDao;
import tools.datasync.seed.SeedConsumerFactory;
import tools.datasync.seed.SeedProducerFactory;

public class SyncPairConfig {

    private GenericDao sourceDao;
    private GenericDao targetDao;
    private SeedConsumerFactory seedConsumerFactory;
    private SeedProducerFactory seedProducerFactory;
    private ConflictResolverFactory conflictResolverFactory;

    public SyncPairConfig(GenericDaoPair daoPair, SeedFactoryPair factoryPair,
	    ConflictResolverFactory conflictResolverFactory) {
	this.sourceDao = daoPair.getSourceDao();
	this.targetDao = daoPair.getTargetDao();
	this.seedConsumerFactory = factoryPair.getSeedConsumerFactory();
	this.seedProducerFactory = factoryPair.getSeedProducerFactory();
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

    public SeedProducerFactory getSeedProducerFactory() {
	return seedProducerFactory;
    }

    public ConflictResolverFactory getConflictResolverFactory() {
	return conflictResolverFactory;
    }

}