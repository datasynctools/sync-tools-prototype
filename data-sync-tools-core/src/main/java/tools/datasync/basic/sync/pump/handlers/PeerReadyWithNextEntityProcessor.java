package tools.datasync.basic.sync.pump.handlers;

import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.basic.comm.SyncMessage;

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

	String thisEntityId = syncMessage.getPayloadJson();
	arrayList.add(thisEntityId);
	LOG.info("Acknowledged Peer ready with next entity "
		+ "message {} to arrayList {}", thisEntityId, arrayList);

	return false; // not finished
    }

}
