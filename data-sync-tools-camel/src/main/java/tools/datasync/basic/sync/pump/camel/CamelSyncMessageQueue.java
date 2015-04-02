package tools.datasync.basic.sync.pump.camel;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.api.msg.SyncMessage;
import tools.datasync.api.msg.SyncMessageFromT;
import tools.datasync.api.utils.Stringify;
import tools.datasync.api.utils.SyncMessageQueue;
import tools.datasync.dataformats.json.Jsonify;
import tools.datasync.dataformats.json.SyncMessageFromJson;
import tools.datasync.utils.StringUtils;

public class CamelSyncMessageQueue implements SyncMessageQueue {

    private static final Logger LOG = LoggerFactory
	    .getLogger(CamelSyncMessageQueue.class);

    private String updateUri;
    private String requestUri;

    private SyncMessageFromT<String> syncMessageFromStringer = new SyncMessageFromJson();
    private Stringify stringify = new Jsonify();

    private ProducerTemplate template;

    public CamelSyncMessageQueue(String updateUri, String requestUri,
	    ProducerTemplate template) {
	this.updateUri = updateUri;
	this.requestUri = requestUri;
	this.template = template;
    }

    public void put(SyncMessage syncMessage) {
	String body = stringify.toString(syncMessage);
	template.sendBody(updateUri, body);
	LOG.info("Sent sync message number {}", syncMessage.getMessageNumber());
    }

    private boolean continueMe(Date start, Date end, long duration) {
	long thisDuration = end.getTime() - start.getTime();
	return (thisDuration < duration);
    }

    public SyncMessage poll(long timeout, TimeUnit unit) {
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
	SyncMessage syncMessage;
	try {
	    syncMessage = syncMessageFromStringer.create(response);
	} catch (Exception e) {
	    LOG.error("Bad data [{}]", response, e);
	    throw (new RuntimeException("Bad Data", e));
	}

	LOG.info("Found SyncMessage of message type {} with msgNum {}",
		syncMessage.getMessageType(), syncMessage.getMessageNumber());
	LOG.debug("SyncMessage {}", syncMessage);

	return (syncMessage);
    }

    public void setSyncMessageFromStringer(
	    SyncMessageFromT<String> syncMessageFromStringer) {
	this.syncMessageFromStringer = syncMessageFromStringer;
    }

    public void setStringify(Stringify stringify) {
	this.stringify = stringify;
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
