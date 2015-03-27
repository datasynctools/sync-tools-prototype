package tools.datasync.basic.sync.pump.camel;

import java.util.concurrent.BlockingQueue;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.api.msg.SyncMessage;

public class CamelProcessorQueue implements Processor {

    private static final Logger LOG = LoggerFactory
	    .getLogger(CamelProcessorQueue.class);

    private BlockingQueue<SyncMessage> blockingQueue;

    public CamelProcessorQueue(BlockingQueue<SyncMessage> blockingQueue) {
	this.blockingQueue = blockingQueue;
    }

    public void process(Exchange exchange) throws Exception {
	String body = exchange.getIn().getBody(String.class);
	LOG.debug("body: [{}]", body);

	SyncMessage msg = blockingQueue.poll();

	LOG.debug("Response 'Out' body: {}", msg);
	// String body = exchange.getIn().getBody(String.class);
	exchange.getOut().setBody(msg);
    }

}
