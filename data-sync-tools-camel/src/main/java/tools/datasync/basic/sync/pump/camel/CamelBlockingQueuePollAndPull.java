package tools.datasync.basic.sync.pump.camel;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.camel.ProducerTemplate;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.api.msg.SyncMessage;
import tools.datasync.basic.util.ObjectMapperFactory;
import tools.datasync.basic.util.StringUtils;
import tools.datasync.data.formats.json.Jsonify;

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
	template.sendBody(updateUri, body);
	LOG.info("Sent sync message number {}", syncMessage.getMessageNumber());

	// LOG.info("Sent sync message type {}, number {}, hash {}",
	// syncMessage.getMessageType(), syncMessage.getMessageNumber(),
	// syncMessage.getPaloadHash());
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

	LOG.info("Found SyncMessage of message type {} with msgNum {}",
		syncMessage.getMessageType(), syncMessage.getMessageNumber());
	LOG.debug("SyncMessage {}", syncMessage);

	return (syncMessage);

    }

    public String toString() {
	StringBuilder answer = new StringBuilder();
	answer.append(StringUtils.getSimpleName(this));
	answer.append("{");
	answer.append("updateUri=");
	answer.append(updateUri);
	answer.append(", ");
	answer.append("requestUri=");
	answer.append(requestUri);
	answer.append("}");
	return (answer.toString());
    }

}
