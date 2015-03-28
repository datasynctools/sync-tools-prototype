package tools.datasync.seed.jdbc;

import tools.datasync.api.conflict.ConflictResolver;
import tools.datasync.api.dao.EntityGetter;
import tools.datasync.api.dao.GenericDao;
import tools.datasync.api.dao.IdGetter;
import tools.datasync.api.utils.HashGenerator;
import tools.datasync.api.utils.Stringify;
import tools.datasync.seed.SeedConsumer;
import tools.datasync.seed.SeedConsumerFactory;

public class DbSeedConsumerFactory implements SeedConsumerFactory {

    private EntityGetter entityGetter;
    private IdGetter recordIdGetter;
    private Stringify stringify;
    private HashGenerator hasher;

    public DbSeedConsumerFactory(EntityGetter entityGetter,
	    IdGetter recordIdGetter) {
	this.entityGetter = entityGetter;
	this.recordIdGetter = recordIdGetter;
    }

    public SeedConsumer create(ConflictResolver conflictResolver,
	    GenericDao genericDao) {
	DbSeedConsumer seedConsumer = new DbSeedConsumer(conflictResolver,
		entityGetter, recordIdGetter, genericDao);
	if (stringify != null)
	    seedConsumer.setStringify(stringify);
	if (hasher != null)
	    seedConsumer.setHasher(hasher);
	return (seedConsumer);
    }

    public Stringify getStringify() {
	return stringify;
    }

    public void setStringify(Stringify stringify) {
	this.stringify = stringify;
    }

    public HashGenerator getHasher() {
	return hasher;
    }

    public void setHasher(HashGenerator hasher) {
	this.hasher = hasher;
    }

}
