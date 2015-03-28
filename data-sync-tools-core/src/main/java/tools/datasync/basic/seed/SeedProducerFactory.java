package tools.datasync.basic.seed;

import java.util.List;

import tools.datasync.api.dao.EntityGetter;
import tools.datasync.api.dao.GenericDao;

public interface SeedProducerFactory {
    SeedProducer create(List<String> tables, EntityGetter entityGetter,
	    GenericDao genericDao);
}
