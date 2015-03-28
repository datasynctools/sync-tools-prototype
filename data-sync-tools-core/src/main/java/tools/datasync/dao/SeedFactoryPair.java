package tools.datasync.dao;

import tools.datasync.basic.seed.SeedConsumerFactory;
import tools.datasync.basic.seed.SeedProducerFactory;

public class SeedFactoryPair {

    private SeedConsumerFactory seedConsumerFactory;
    private SeedProducerFactory seedProducerFactory;

    public SeedFactoryPair(SeedConsumerFactory seedConsumerFactory,
	    SeedProducerFactory seedProducerFactory) {
	this.seedConsumerFactory = seedConsumerFactory;
	this.seedProducerFactory = seedProducerFactory;
    }

    public SeedConsumerFactory getSeedConsumerFactory() {
	return seedConsumerFactory;
    }

    public SeedProducerFactory getSeedProducerFactory() {
	return seedProducerFactory;
    }

}
