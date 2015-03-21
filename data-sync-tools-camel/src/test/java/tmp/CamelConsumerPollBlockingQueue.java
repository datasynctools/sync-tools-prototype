package tmp;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.basic.sync.pump.camel.AbstractPartialBlockingQueue;

public class CamelConsumerPollBlockingQueue extends AbstractPartialBlockingQueue
	implements BlockingQueue<String> {

    private static final Logger LOG = LoggerFactory
	    .getLogger(CamelConsumerPollBlockingQueue.class);

    private ConsumerTemplate template;
    private String uri;

    private BlockingQueue<String> producerBlockingQueue;

    public CamelConsumerPollBlockingQueue(ConsumerTemplate template, String uri,
	    BlockingQueue<String> producerBlockingQueue) {
	this.template = template;
	this.uri = uri;
	this.producerBlockingQueue = producerBlockingQueue;
    }

    public void put(String e) throws InterruptedException {
	LOG.info("Sending body {}", e);
	producerBlockingQueue.put(e);
    }

    public String poll(long timeout, TimeUnit unit) throws InterruptedException {
	long start = new Date().getTime();
	long end;
	long duration;
	long endAfterMs = TimeUnit.MILLISECONDS.convert(timeout, unit);
	String body = null;
	while (body == null) {
	    end = new Date().getTime();
	    duration = end - start;
	    if (duration >= endAfterMs) {
		LOG.info("timed out, returning");
		return null;
	    }
	    body = doRequest();

	}
	return body;
    }

    private String doRequest() {
	LOG.info("Request from {}", uri);
	Exchange exchange = template.receive(uri);
	if (!exchange.isFailed()) {
	    nullBodyGuard(exchange);
	    String body = exchange.getOut().getBody(String.class);

	    if (!exchange.getOut().isFault()) {
		return (handleNormal(body));
	    } else {
		return (handleFault(body));
	    }
	}
	return (handleFailedExchange(exchange));
    }

    private void nullBodyGuard(Exchange exchange) {
	if (exchange.getOut().getBody() == null) {
	    String msg = "Unexpected null message on camel route response for uri = ["
		    + uri + "]";
	    LOG.error(msg);
	    throw (new RuntimeException(msg));
	}
    }

    private String handleFailedExchange(Exchange exchange) {
	String msg = "Route failed for uri = [" + uri + "], exception: "
		+ exchange.getException().getMessage();
	LOG.error(msg);
	throw (new RuntimeException(msg));

    }

    private String handleFault(String body) {
	String msg = "Route failed for uri = [" + uri + "], exception: " + body;
	LOG.error(msg);
	throw (new RuntimeException(msg));
    }

    private String handleNormal(String body) {
	LOG.info("Received response from {} with {}", uri, body);
	return body;

    }
}
