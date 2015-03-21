package tools.datasync.basic.sync.pump;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.basic.comm.SyncMessage;
import tools.datasync.basic.comm.SyncMessageType;
import tools.datasync.basic.util.JsonMapperBean;

public class BlockingQueueNextEntitySignaler implements NextEntitySignaler {

    private static final Logger LOG = LoggerFactory
	    .getLogger(BlockingQueueNextEntitySignaler.class);

    private BlockingQueue<String> queue;
    private JsonMapperBean jsonMapper;

    public BlockingQueueNextEntitySignaler(BlockingQueue<String> queue) {
	this.queue = queue;
	this.jsonMapper = JsonMapperBean.getInstance();
    }

    @Override
    public void tellPeerReadyForNextEntity(String previousEntityId) {

	SyncMessage syncMessage = new SyncMessage();
	// TODO: ADD Message Number capability to
	syncMessage.setMessageNumber(-1);
	syncMessage.setPayloadJson(previousEntityId);
	syncMessage.setMessageType(SyncMessageType.PEER_READY_WITH_NEXT_ENTITY
		.toString());
	syncMessage.setTimestamp(System.currentTimeMillis());

	String payload;
	try {
	    payload = jsonMapper.writeValueAsString(syncMessage);
	} catch (Exception e) {
	    throw (new RuntimeException(e));
	}

	LOG.info("Sending body {}", payload);
	queue.add(payload);
    }

}
