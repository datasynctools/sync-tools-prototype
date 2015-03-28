package tools.datasync.seed.jdbc;

import java.util.List;

import tools.datasync.api.dao.EntityGetter;
import tools.datasync.api.dao.GenericDao;
import tools.datasync.seed.SeedProducer;
import tools.datasync.seed.SeedProducerFactory;

public class DbSeedProducerFactory implements SeedProducerFactory {

    @Override
    public SeedProducer create(List<String> tables, EntityGetter entityGetter,
	    GenericDao genericDao) {
	DbSeedProducer seedProducer = new DbSeedProducer(genericDao, tables,
		entityGetter);
	return (seedProducer);
    }

}
