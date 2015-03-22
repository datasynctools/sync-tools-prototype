package tools.datasync.basic.sync.pump.camel;

import java.util.concurrent.BlockingQueue;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.basic.comm.SyncMessage;
import tools.datasync.basic.util.ObjectMapperFactory;

public class CamelRequestProcessor implements Processor {

    private static final Logger LOG = LoggerFactory
	    .getLogger(CamelRequestProcessor.class);

    private ObjectMapper jsonMapper = ObjectMapperFactory.getInstance();

    private BlockingQueue<SyncMessage> receiveQueue;

    public CamelRequestProcessor(BlockingQueue<SyncMessage> receiveQueue) {
	this.receiveQueue = receiveQueue;
    }

    public void process(Exchange exchange) throws Exception {
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

}
