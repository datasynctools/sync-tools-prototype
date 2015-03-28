package tools.datasync.basic.seed;

import java.util.List;

import tools.datasync.api.dao.EntityGetter;
import tools.datasync.api.dao.GenericDao;

public class DbSeedProducerFactory implements SeedProducerFactory {

    @Override
    public SeedProducer create(List<String> tables, EntityGetter entityGetter,
	    GenericDao genericDao) {
	DbSeedProducer seedProducer = new DbSeedProducer(genericDao, tables,
		entityGetter);
	return (seedProducer);
    }

}
