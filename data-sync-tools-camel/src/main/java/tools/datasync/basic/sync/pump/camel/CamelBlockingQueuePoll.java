package tools.datasync.basic.sync.pump.camel;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.camel.ProducerTemplate;

public class CamelBlockingQueuePoll extends
	AbstractPartialBlockingQueue implements BlockingQueue<String> {

    // private static final Logger LOG = LoggerFactory
    // .getLogger(CamelProducerPollBlockingQueue.class);

    private ProducerTemplate template;

    private BlockingQueue<String> consumingBlockingQueue;

    public CamelBlockingQueuePoll(ProducerTemplate template,
	    BlockingQueue<String> consumingBlockingQueue) {
	this.template = template;
	this.consumingBlockingQueue = consumingBlockingQueue;
    }

    public void put(String e) throws InterruptedException {
	consumingBlockingQueue.put(e);
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
	    String response = template.requestBody((Object) obj, String.class);

	    if (!response.isEmpty()) {
		return (response);
	    }
	    end = new Date();

	} while (continueMe(start, end, duration));

	return null;

    }

}
