package tools.datasync.basic.sync.pump.camel;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.camel.ProducerTemplate;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.basic.comm.SyncMessage;
import tools.datasync.basic.util.ObjectMapperFactory;

public class CamelBlockingQueuePoll extends AbstractPartialBlockingQueue
	implements BlockingQueue<SyncMessage> {

    private static final Logger LOG = LoggerFactory
	    .getLogger(CamelBlockingQueuePoll.class);

    private ProducerTemplate template;

    private BlockingQueue<SyncMessage> consumingBlockingQueue;

    private ObjectMapper jsonMapper = ObjectMapperFactory.getInstance();

    public CamelBlockingQueuePoll(ProducerTemplate template,
	    BlockingQueue<SyncMessage> consumingBlockingQueue) {
	this.template = template;
	this.consumingBlockingQueue = consumingBlockingQueue;
    }

    public void put(SyncMessage e) throws InterruptedException {
	consumingBlockingQueue.put(e);
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

	do {
	    String response = template.requestBody((Object) obj, String.class);

	    if (!response.isEmpty()) {
		return (processResponse(response));
	    }
	    end = new Date();

	} while (continueMe(start, end, duration));

	return null;

    }

    private SyncMessage processResponse(String response) {
	SyncMessage syncMessage = null;
	try {
	    syncMessage = jsonMapper.readValue(response, SyncMessage.class);
	} catch (Exception e) {
	    LOG.error("Bad data", e);
	    throw (new RuntimeException("Bad Data", e));
	}

	LOG.info("Found message {}", syncMessage);

	return (syncMessage);

    }

}
