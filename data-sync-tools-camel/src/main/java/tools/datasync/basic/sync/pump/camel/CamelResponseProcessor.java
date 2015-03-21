package tools.datasync.basic.sync.pump.camel;

import java.util.concurrent.BlockingQueue;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamelResponseProcessor implements Processor {

    private static final Logger LOG = LoggerFactory
	    .getLogger(CamelResponseProcessor.class);

    private BlockingQueue<String> queue;

    public CamelResponseProcessor(BlockingQueue<String> queue) {
	this.queue = queue;
    }

    public void process(Exchange exchange) throws Exception {
	// just get the body as a string
	String body = queue.poll();
	String msg;
	if (body == null) {
	    msg = null;
	} else {
	    msg = body;
	    LOG.info("Responding body {}", msg);
	}
	exchange.getOut().setBody(msg);
    }

}
