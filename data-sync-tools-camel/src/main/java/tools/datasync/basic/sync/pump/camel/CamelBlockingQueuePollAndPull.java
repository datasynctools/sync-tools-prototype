package tools.datasync.basic.sync.pump.camel;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.camel.ProducerTemplate;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.api.utils.Jsonify;
import tools.datasync.basic.comm.SyncMessage;
import tools.datasync.basic.util.ObjectMapperFactory;

public class CamelBlockingQueuePollAndPull extends AbstractPartialBlockingQueue
	implements BlockingQueue<SyncMessage> {

    private static final Logger LOG = LoggerFactory
	    .getLogger(CamelBlockingQueuePollAndPull.class);

    private String updateUri;
    private String requestUri;

    private ObjectMapper jsonMapper = ObjectMapperFactory.getInstance();
    private Jsonify jsonify = new Jsonify();

    private ProducerTemplate template;

    public CamelBlockingQueuePollAndPull(String updateUri, String requestUri,
	    ProducerTemplate template) {
	this.updateUri = updateUri;
	this.requestUri = requestUri;
	this.template = template;
    }

    public void put(SyncMessage syncMessage) throws InterruptedException {
	String body = jsonify.toString(syncMessage);
	LOG.info("Sending body {}", body);
	template.sendBody(updateUri, body);
    }

    private boolean continueMe(Date start, Date end, long duration) {
	long thisDuration = end.getTime() - start.getTime();
	return (thisDuration < duration);
    }

    public SyncMessage poll(long timeout, TimeUnit unit)
	    throws InterruptedException {

	String obj = "";
	Date start = new Date();
	Date end = new Date();
	long duration = TimeUnit.MILLISECONDS.convert(timeout, unit);

	LOG.debug("Getting messages from {}", requestUri);

	do {
	    String response = template.requestBody(requestUri, (Object) obj,
		    String.class);

	    if (!response.isEmpty()) {

		return (processResponse(response));

	    }
	    end = new Date();

	} while (continueMe(start, end, duration));
	LOG.debug("No message in timeout period");
	return null;

    }

    private SyncMessage processResponse(String response) {
	SyncMessage syncMessage = null;
	try {
	    syncMessage = jsonMapper.readValue(response, SyncMessage.class);
	} catch (Exception e) {
	    LOG.error("Bad data [{}]", response, e);
	    throw (new RuntimeException("Bad Data", e));
	}

	LOG.info("Found message {}", syncMessage);

	return (syncMessage);

    }

}
