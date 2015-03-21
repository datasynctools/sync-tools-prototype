package tools.datasync.basic.sync.pump.camel;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamelBlockingQueuePut extends AbstractPartialBlockingQueue
	implements BlockingQueue<String> {

    private static final Logger LOG = LoggerFactory
	    .getLogger(CamelBlockingQueuePut.class);

    private ProducerTemplate template;

    private BlockingQueue<String> consumingBlockingQueue;

    public CamelBlockingQueuePut(ProducerTemplate template,
	    BlockingQueue<String> consumingBlockingQueue) {
	this.template = template;
	this.consumingBlockingQueue = consumingBlockingQueue;
    }

    public void put(String e) throws InterruptedException {
	LOG.info("Sending body {}", e);
	template.sendBody(e);
    }

    public String poll(long timeout, TimeUnit unit) throws InterruptedException {
	return (consumingBlockingQueue.poll(timeout, unit));
    }

}
