package tools.datasync.basic.sync.pump.camel;

import java.util.concurrent.BlockingQueue;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.api.utils.Jsonify;
import tools.datasync.basic.comm.SyncMessage;
import tools.datasync.basic.util.StringUtils;

public class CamelResponseProcessor implements Processor {

    private static final Logger LOG = LoggerFactory
	    .getLogger(CamelResponseProcessor.class);

    private BlockingQueue<SyncMessage> queue;

    private Jsonify jsonify = new Jsonify();

    private boolean firstRun = true;

    public CamelResponseProcessor(BlockingQueue<SyncMessage> queue) {
	this.queue = queue;
    }

    private void startMsg(String fromEndPointUri) {
	LOG.info("First Message Camel Processor: " + this.toString()
		+ ", fromEndPointUri=" + fromEndPointUri);
    }

    public void process(Exchange exchange) throws Exception {

	if (firstRun) {
	    startMsg(exchange.getFromEndpoint().getEndpointUri());
	    firstRun = false;
	}

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

    private void addQueues(StringBuilder answer) {
	answer.append("queue=");
	answer.append(queue.toString());
	answer.append(", ");
	answer.append("queueClass=");
	answer.append(StringUtils.getSimpleName(queue));
	answer.append(", ");
	answer.append("queueInstanceHashCode=");
	answer.append(queue.hashCode());
    }

    public String toString() {
	StringBuilder answer = new StringBuilder();
	answer.append(StringUtils.getSimpleName(this));
	answer.append("{");
	addQueues(answer);
	answer.append("}");
	return (answer.toString());
    }

}