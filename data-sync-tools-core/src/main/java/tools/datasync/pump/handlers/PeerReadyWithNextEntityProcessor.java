package tools.datasync.pump.handlers;

import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.api.msg.SyncMessage;
import tools.datasync.model.EnityId;

public class PeerReadyWithNextEntityProcessor implements SyncMessageProcessor {

    private static final Logger LOG = LoggerFactory
	    .getLogger(PeerReadyWithNextEntityProcessor.class);

    private CopyOnWriteArrayList<String> arrayList;

    public PeerReadyWithNextEntityProcessor(
	    CopyOnWriteArrayList<String> arrayList) {
	this.arrayList = arrayList;
    }

    @Override
    public boolean handle(SyncMessage syncMessage) {

	EnityId entityIdObj = (EnityId) syncMessage.getPayloadData();
	String thisEntityId = entityIdObj.getEntityId().toString();
	arrayList.add(thisEntityId);
	LOG.info("Acknowledged Peer ready with next entity "
		+ "message {} to arrayList {}", thisEntityId, arrayList);

	return false; // not finished
    }

}
