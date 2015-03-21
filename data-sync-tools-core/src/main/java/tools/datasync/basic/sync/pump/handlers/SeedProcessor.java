package tools.datasync.basic.sync.pump.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.basic.comm.SyncMessage;
import tools.datasync.basic.model.SeedRecord;
import tools.datasync.basic.seed.SeedConsumer;
import tools.datasync.basic.sync.pump.NextEntitySignaler;
import tools.datasync.basic.util.JsonMapperBean;

public class SeedProcessor implements SyncMessageProcessor {

    private static final Logger LOG = LoggerFactory
	    .getLogger(SeedProcessor.class);

    private JsonMapperBean jsonMapper;
    private SeedConsumer seedConsumer;
    private String lastEntityId = null;
    private NextEntitySignaler nextEntitySignaler;

    public SeedProcessor(SeedConsumer seedConsumer,
	    NextEntitySignaler nextEntitySignaler) {
	this.seedConsumer = seedConsumer;
	this.nextEntitySignaler = nextEntitySignaler;
	this.jsonMapper = JsonMapperBean.getInstance();
    }

    @Override
    public boolean handle(SyncMessage syncMessage) {

	SeedRecord seed;
	try {
	    seed = jsonMapper.readValue(syncMessage.getPayloadJson(),
		    SeedRecord.class);
	    seedConsumer.consume(seed);

	} catch (Exception e) {
	    LOG.error("Fatal exception in processing", e);
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
