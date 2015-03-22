package tools.datasync.basic.sync.pump.camel;

import static org.junit.Assert.fail;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultProducerTemplate;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.basic.comm.SyncMessage;

public class CamelProducerPutBlockingQueueTests {

    private static final Logger LOG = LoggerFactory
	    .getLogger(CamelProducerPutBlockingQueueTests.class);

    @Test
    public void testPut() {
	fail("Not yet implemented");
    }

    private BlockingQueue<SyncMessage> buildCamelObjects(
	    BlockingQueue<SyncMessage> blockingQueue, String fromUri,
	    String toUri, CamelContext camelContext) throws Exception {

	Processor processor = new CamelProcessorQueue(blockingQueue);
	RoutesBuilder builder = new TestRouteBuilder(fromUri, processor);
	camelContext.addRoutes(builder);

	Endpoint endpoint = camelContext.getEndpoint(toUri);
	ProducerTemplate producerTemplate = new DefaultProducerTemplate(
		camelContext, endpoint);

	CamelBlockingQueuePoll producerQueue = new CamelBlockingQueuePoll(
		producerTemplate, blockingQueue);

	camelContext.start();
	producerTemplate.start();

	return producerQueue;
    }

    @Test
    public void testPollLongTimeUnit() throws Exception {
	CamelContext camelContext = new DefaultCamelContext();

	String fromUri = "jetty:http://localhost:10010/request";

	String toUri = "http4:localhost:10010/request";

	BlockingQueue<SyncMessage> blockingQueue = new LinkedBlockingQueue<SyncMessage>();

	BlockingQueue<SyncMessage> camelBlockingQueue = buildCamelObjects(
		blockingQueue, fromUri, toUri, camelContext);

	LOG.info("Getting null");
	SyncMessage val = camelBlockingQueue.poll(2, TimeUnit.SECONDS);
	Assert.assertNull(val);
	LOG.info("val [{}]", val);

	// TODO FIX ME after refactoring to use generics
	SyncMessage expected = null;// "my val";
	blockingQueue.put(expected);
	LOG.info("Getting value");
	val = camelBlockingQueue.poll(2, TimeUnit.SECONDS);
	Assert.assertEquals(expected, val);
	LOG.info("val [{}]", val);
    }
}
