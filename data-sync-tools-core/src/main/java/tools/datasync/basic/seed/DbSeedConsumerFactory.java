package tools.datasync.basic.seed;

import tools.datasync.basic.dao.GenericDao;
import tools.datasync.basic.logic.ConflictResolver;
import tools.datasync.basic.model.EntityGetter;
import tools.datasync.basic.model.IdGetter;

public class DbSeedConsumerFactory implements SeedConsumerFactory {

    private EntityGetter entityGetter;
    private IdGetter recordIdGetter;

    public DbSeedConsumerFactory(EntityGetter entityGetter,
	    IdGetter recordIdGetter) {
	this.entityGetter = entityGetter;
	this.recordIdGetter = recordIdGetter;
    }

    public SeedConsumer create(ConflictResolver conflictResolver,
	    GenericDao genericDao) {
	return new DbSeedConsumer(conflictResolver, entityGetter,
		recordIdGetter, genericDao);
    }

}
