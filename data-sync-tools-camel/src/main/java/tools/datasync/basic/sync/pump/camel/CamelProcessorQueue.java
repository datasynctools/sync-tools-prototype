package tools.datasync.basic.sync.pump.camel;

import java.util.concurrent.BlockingQueue;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamelProcessorQueue implements Processor {

    private static final Logger LOG = LoggerFactory
	    .getLogger(CamelProcessorQueue.class);

    private BlockingQueue<String> blockingQueue;

    public CamelProcessorQueue(BlockingQueue<String> blockingQueue) {
	this.blockingQueue = blockingQueue;
    }

    public void process(Exchange exchange) throws Exception {
	String body = exchange.getIn().getBody(String.class);
	LOG.debug("body: [{}]", body);

	String msg = blockingQueue.poll();

	LOG.debug("Response 'Out' body: {}", msg);
	// String body = exchange.getIn().getBody(String.class);
	exchange.getOut().setBody(msg);
    }

}
