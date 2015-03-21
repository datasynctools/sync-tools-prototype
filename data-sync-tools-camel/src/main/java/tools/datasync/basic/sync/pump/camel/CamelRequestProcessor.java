package tools.datasync.basic.sync.pump.camel;

import java.util.concurrent.BlockingQueue;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamelRequestProcessor implements Processor {

    private static final Logger LOG = LoggerFactory
	    .getLogger(CamelRequestProcessor.class);

    private BlockingQueue<String> receiveQueue;

    public CamelRequestProcessor(BlockingQueue<String> receiveQueue) {
	this.receiveQueue = receiveQueue;
    }

    public void process(Exchange exchange) throws Exception {
	// just get the body as a string
	String body = exchange.getIn().getBody(String.class);
	LOG.info("processed to queue: {}", body);
	receiveQueue.put(body);
    }

}
