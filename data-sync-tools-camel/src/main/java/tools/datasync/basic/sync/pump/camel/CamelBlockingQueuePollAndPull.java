package tools.datasync.basic.sync.pump.camel;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamelBlockingQueuePollAndPull extends AbstractPartialBlockingQueue
	implements BlockingQueue<String> {

    private static final Logger LOG = LoggerFactory
	    .getLogger(CamelBlockingQueuePollAndPull.class);

    private String updateUri;
    private String requestUri;

    private ProducerTemplate template;

    public CamelBlockingQueuePollAndPull(String updateUri, String requestUri,
	    ProducerTemplate template) {
	this.updateUri = updateUri;
	this.requestUri = requestUri;
	this.template = template;
    }

    public void put(String e) throws InterruptedException {
	LOG.info("Sending body {}", e);
	template.sendBody(updateUri, e);
    }

    private boolean continueMe(Date start, Date end, long duration) {
	long thisDuration = end.getTime() - start.getTime();
	return (thisDuration < duration);
    }

    public String poll(long timeout, TimeUnit unit) throws InterruptedException {

	String obj = "";
	Date start = new Date();
	Date end = new Date();
	long duration = TimeUnit.MILLISECONDS.convert(timeout, unit);

	do {
	    String response = template.requestBody(requestUri, (Object) obj,
		    String.class);

	    if (!response.isEmpty()) {
		LOG.info("Found message {}", response);

		return (response);
	    }
	    end = new Date();

	} while (continueMe(start, end, duration));
	LOG.debug("No message in timeout period");
	return null;

    }

}
