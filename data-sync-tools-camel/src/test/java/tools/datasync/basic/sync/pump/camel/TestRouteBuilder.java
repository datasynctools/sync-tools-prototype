package tools.datasync.basic.sync.pump.camel;

import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

public class TestRouteBuilder extends RouteBuilder {

    private String fromUri;
    private Processor toProcessor;

    public TestRouteBuilder(String fromUri, Processor toProcessor) {
	this.fromUri = fromUri;
	this.toProcessor = toProcessor;
    }

    @Override
    public void configure() throws Exception {
	from(fromUri).process(toProcessor);
    }
}
