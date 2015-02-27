package tools.datasync.basic.seed;

import tools.datasync.basic.dao.GenericDao;
import tools.datasync.basic.logic.ConflictResolver;
import tools.datasync.basic.model.IdGetter;

public class DbSeedConsumerFactory implements SeedConsumerFactory {

    private IdGetter idGetter;

    public DbSeedConsumerFactory(IdGetter idGetter) {
	this.idGetter = idGetter;
    }

    public SeedConsumer create(ConflictResolver conflictResolver,
	    GenericDao genericDao) {
	return new DbSeedConsumer(conflictResolver, idGetter, genericDao);
    }

}
