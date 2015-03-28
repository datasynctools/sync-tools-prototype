package tools.datasync.pump;

import static tools.datasync.api.msg.SyncMessageType.PEER_READY_WITH_NEXT_ENTITY;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.api.msg.SyncMessage;
import tools.datasync.model.EnityId;
import tools.datasync.utils.StringUtils;

//TODO Consider this component being part of the Sender.
//Perhaps we should passing a message to a receiver->sender queue and then the sender 
//is responsible to getting the message to the peer. This is not functional per say, 
//but this is the ONLY component that current sends messages to the peer from the 
//Receiver. Specifically, this gap is shown in the log files for the newly broken out 
//log files by sender, receiver, thread, and http. This makes it harder to look at 
//the sender and know what's going on. It also confuses messages in the receiver log
//as all of the logs show incoming messages but this is ONLY one that is outgoing.
//This will confuse future views of these logs whether they be developers, administrators,
//or architects.
public class BlockingQueueNextEntitySignaler implements NextEntitySignaler {

    private static final Logger LOG = LoggerFactory
	    .getLogger(BlockingQueueNextEntitySignaler.class);

    private BlockingQueue<SyncMessage> queue;

    public BlockingQueueNextEntitySignaler(BlockingQueue<SyncMessage> queue) {
	this.queue = queue;
    }

    public void tellPeerReadyForNextEntity(String previousEntityId) {

	SyncMessage syncMessage = new SyncMessage();
	// TODO: ADD Message Number capability to handle message numbers from
	// Sender (probably a common using AtomicLong)
	syncMessage.setMessageNumber(-1);
	EnityId entityIdObj = new EnityId();
	entityIdObj.setEntityId(previousEntityId);
	syncMessage.setPayloadData(entityIdObj);
	syncMessage.setMessageType(PEER_READY_WITH_NEXT_ENTITY);
	syncMessage.setTimestamp(System.currentTimeMillis());

	LOG.info("Telling peer that this component is ready for next "
		+ "entity processing, through sync message of "
		+ "type {}, number {}", syncMessage.getMessageType(),
		syncMessage.getMessageNumber());

	queue.add(syncMessage);
    }

    public String toString() {
	StringBuilder answer = new StringBuilder();
	answer.append(StringUtils.getSimpleName(this));
	answer.append("{");
	answer.append("queueInstanceHashCode=");
	answer.append(queue.hashCode());
	answer.append("}");
	return (answer.toString());
    }

}
