package tools.datasync.basic.sync.pump;

import java.util.concurrent.BlockingQueue;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.basic.comm.SyncMessage;
import tools.datasync.basic.comm.SyncMessageType;
import tools.datasync.basic.model.EnityId;
import tools.datasync.basic.util.ObjectMapperFactory;

public class BlockingQueueNextEntitySignaler implements NextEntitySignaler {

    private static final Logger LOG = LoggerFactory
	    .getLogger(BlockingQueueNextEntitySignaler.class);

    private BlockingQueue<SyncMessage> queue;
    private ObjectMapper jsonMapper = ObjectMapperFactory.getInstance();

    public BlockingQueueNextEntitySignaler(BlockingQueue<SyncMessage> queue) {
	this.queue = queue;
    }

    @Override
    public void tellPeerReadyForNextEntity(String previousEntityId) {

	SyncMessage syncMessage = new SyncMessage();
	// TODO: ADD Message Number capability to
	syncMessage.setMessageNumber(-1);
	EnityId entityIdObj = new EnityId();
	entityIdObj.setEntityId(previousEntityId);
	syncMessage.setPayloadData(entityIdObj);
	syncMessage.setMessageType(SyncMessageType.PEER_READY_WITH_NEXT_ENTITY
		.toString());
	syncMessage.setTimestamp(System.currentTimeMillis());

	LOG.info("Sending body {}", syncMessage);
	queue.add(syncMessage);
    }

}
