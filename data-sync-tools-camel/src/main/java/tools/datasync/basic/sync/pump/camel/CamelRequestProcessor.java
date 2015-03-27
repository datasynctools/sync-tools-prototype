package tools.datasync.basic.sync.pump.camel;

import java.util.concurrent.BlockingQueue;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.api.msg.SyncMessage;
import tools.datasync.basic.util.ObjectMapperFactory;
import tools.datasync.basic.util.StringUtils;

public class CamelRequestProcessor implements Processor {

    private static final Logger LOG = LoggerFactory
	    .getLogger(CamelRequestProcessor.class);

    private ObjectMapper jsonMapper = ObjectMapperFactory.getInstance();

    private BlockingQueue<SyncMessage> receiveQueue;

    private boolean firstRun = true;

    public CamelRequestProcessor(BlockingQueue<SyncMessage> receiveQueue) {
	this.receiveQueue = receiveQueue;
	LOG.info("Loaded Processor for Camel: " + this);
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
	String body = exchange.getIn().getBody(String.class);

	SyncMessage syncMessage = processResponse(body);

	receiveQueue.put(syncMessage);
	LOG.info("Processed sync message body to queue: {}", body);
    }

    private SyncMessage processResponse(String response) {
	SyncMessage syncMessage = null;
	try {
	    syncMessage = jsonMapper.readValue(response, SyncMessage.class);
	} catch (Exception e) {
	    LOG.error("Bad data [{}]", response, e);
	    throw (new RuntimeException("Bad Data", e));
	}

	LOG.debug("Found message {}", syncMessage);

	return (syncMessage);

    }

    private void addQueues(StringBuilder answer) {
	answer.append("receiveQueue=");
	answer.append(receiveQueue.toString());
	answer.append(", ");
	answer.append("receiveQueueClass=");
	answer.append(StringUtils.getSimpleName(receiveQueue));
	answer.append(", ");
	answer.append("queueInstanceHashCode=");
	answer.append(receiveQueue.hashCode());
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
