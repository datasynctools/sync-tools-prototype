package tools.datasync.basic.sync.pump.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.basic.comm.SyncMessage;

public class SyncOverProcessor implements SyncMessageProcessor {

    private static final Logger LOG = LoggerFactory
	    .getLogger(SyncOverProcessor.class);

    public boolean handle(SyncMessage syncMessage) {

	LOG.info("Finished sync for this receiver");

	return true; // finished
    }

}