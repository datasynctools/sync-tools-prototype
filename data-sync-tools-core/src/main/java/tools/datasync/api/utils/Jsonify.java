package tools.datasync.api.utils;

import java.io.StringWriter;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.node.ObjectNode;

public class Jsonify implements Stringify {

    public String toString(Object item) {
	StringWriter writer = new StringWriter();
	ObjectMapper mapper = new ObjectMapper();
	ObjectNode root = mapper.createObjectNode();
	root.putPOJO(item.getClass().getSimpleName(), item);
	// mapper.enable(SerializationConfig.Feature.FLUSH_AFTER_WRITE_VALUE);
	mapper.disable(SerializationConfig.Feature.INDENT_OUTPUT);
	try {
	    mapper.writeValue(writer, root);
	} catch (Exception e) {
	    throw (new RuntimeException(e));
	}
	return writer.toString();

    }
}
