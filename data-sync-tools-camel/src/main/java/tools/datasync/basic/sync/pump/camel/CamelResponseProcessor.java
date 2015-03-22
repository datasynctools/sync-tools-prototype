package tools.datasync.basic.sync.pump.camel;

import java.util.concurrent.BlockingQueue;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.api.utils.Jsonify;
import tools.datasync.basic.comm.SyncMessage;

public class CamelResponseProcessor implements Processor {

    private static final Logger LOG = LoggerFactory
	    .getLogger(CamelResponseProcessor.class);

    private BlockingQueue<SyncMessage> queue;

    private Jsonify jsonify = new Jsonify();

    public CamelResponseProcessor(BlockingQueue<SyncMessage> queue) {
	this.queue = queue;
    }

    public void process(Exchange exchange) throws Exception {
	// just get the body as a string
	SyncMessage syncMessage = queue.poll();
	String body;
	if (syncMessage == null) {
	    body = null;
	} else {
	    body = jsonify.toStringSerialize(syncMessage);
	    LOG.info("Responding body {}", body);
	}
	exchange.getOut().setBody(body);
    }

}
