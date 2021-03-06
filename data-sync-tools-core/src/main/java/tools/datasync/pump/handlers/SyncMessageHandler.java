package tools.datasync.pump.handlers;

import static tools.datasync.api.msg.SyncMessageType.BEGIN_SEED;
import static tools.datasync.api.msg.SyncMessageType.PEER_READY_WITH_NEXT_ENTITY;
import static tools.datasync.api.msg.SyncMessageType.SEED;
import static tools.datasync.api.msg.SyncMessageType.SYNC_OVER;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.api.msg.SyncMessage;
import tools.datasync.api.utils.Stringify;
import tools.datasync.dataformats.json.Jsonify;
import tools.datasync.pump.NextEntitySignaler;
import tools.datasync.seed.SeedConsumer;

public class SyncMessageHandler {

    private static final Logger LOG = LoggerFactory
	    .getLogger(SyncMessageHandler.class);

    private Map<String, SyncMessageProcessor> processors = new HashMap<String, SyncMessageProcessor>();

    private Stringify stringifier = new Jsonify();

    public SyncMessageHandler(CountDownLatch latch,
	    CopyOnWriteArrayList<String> arrayList, SeedConsumer seedConsumer,
	    NextEntitySignaler nextEntitySignaler) {
	processors.put(BEGIN_SEED, new BeginSeedProcessor(latch));
	processors.put(SYNC_OVER, new SyncOverProcessor());
	processors.put(PEER_READY_WITH_NEXT_ENTITY,
		new PeerReadyWithNextEntityProcessor(arrayList));
	processors.put(SEED,
		new SeedProcessor(seedConsumer, nextEntitySignaler));

    }

    public boolean handle(SyncMessage syncMessage) {

	LOG.debug("Received Sync Message: " + stringifier.toString(syncMessage));
	// LOG.info("Received Sync Message: \n"
	// + stringifier.toStringPretty(syncMessage));

	SyncMessageProcessor processor = processors.get(syncMessage
		.getMessageType());

	if (processor == null) {
	    LOG.warn("Unknown message");
	    return false; // not finished
	}

	return (processor.handle(syncMessage));

    }

    // TODO this object is not set when loaded, fix this so we are not bound to
    // the Json implementation
    public void setStringifier(Stringify stringifier) {
	this.stringifier = stringifier;
    }

}
