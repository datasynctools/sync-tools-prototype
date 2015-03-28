package tools.datasync.basic.sync.pump.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.api.msg.SyncMessage;
import tools.datasync.basic.model.SeedRecord;
import tools.datasync.basic.sync.pump.NextEntitySignaler;
import tools.datasync.seed.SeedConsumer;

public class SeedProcessor implements SyncMessageProcessor {

    private static final Logger LOG = LoggerFactory
	    .getLogger(SeedProcessor.class);
    private SeedConsumer seedConsumer;
    private String lastEntityId = null;
    private NextEntitySignaler nextEntitySignaler;

    public SeedProcessor(SeedConsumer seedConsumer,
	    NextEntitySignaler nextEntitySignaler) {
	this.seedConsumer = seedConsumer;
	this.nextEntitySignaler = nextEntitySignaler;
    }

    @Override
    public boolean handle(SyncMessage syncMessage) {

	SeedRecord seed;
	try {

	    seed = (SeedRecord) syncMessage.getPayloadData();
	    seedConsumer.consume(seed);

	} catch (Exception e) {
	    LOG.error("Fatal exception in processing syncMessage {}",
		    syncMessage, e);
	    throw (new RuntimeException("Fatal exception in processing", e));
	}

	String thisEntityId = seed.getEntityId();
	if (lastEntityId == null) {
	    lastEntityId = thisEntityId;
	    // first time processing, so set the entityId
	}
	if (!lastEntityId.equals(thisEntityId)) {
	    // signal that we're done processing this entity to the peer
	    nextEntitySignaler.tellPeerReadyForNextEntity(lastEntityId);
	    lastEntityId = thisEntityId;
	}

	return false; // not finished
    }

}
