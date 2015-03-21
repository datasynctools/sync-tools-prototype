package tools.datasync.basic.sync.pump.handlers;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.basic.comm.SyncMessage;
import tools.datasync.basic.comm.SyncMessageType;
import tools.datasync.basic.seed.SeedConsumer;
import tools.datasync.basic.sync.pump.NextEntitySignaler;

public class SyncMessageHandler {

    private static final Logger LOG = LoggerFactory
	    .getLogger(SyncMessageHandler.class);

    private Map<String, SyncMessageProcessor> processors = new HashMap<String, SyncMessageProcessor>();

    public SyncMessageHandler(CountDownLatch latch,
	    CopyOnWriteArrayList<String> arrayList, SeedConsumer seedConsumer,
	    NextEntitySignaler nextEntitySignaler) {
	processors.put(SyncMessageType.BEGIN_SEED.toString(),
		new BeginSeedProcessor(latch));
	processors.put(SyncMessageType.SYNC_OVER.toString(),
		new SyncOverProcessor());
	processors.put(SyncMessageType.PEER_READY_WITH_NEXT_ENTITY.toString(),
		new PeerReadyWithNextEntityProcessor(arrayList));
	processors.put(SyncMessageType.SEED.toString(), new SeedProcessor(
		seedConsumer, nextEntitySignaler));

    }

    public boolean handle(SyncMessage syncMessage) {

	LOG.info("Received Sync Message: " + syncMessage);

	SyncMessageProcessor processor = processors.get(syncMessage
		.getMessageType());

	if (processor == null) {
	    LOG.warn("Unknown message");
	    return false; // not finished
	}

	return (processor.handle(syncMessage));

    }
}
