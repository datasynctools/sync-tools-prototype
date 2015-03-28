package tools.datasync.dataformats.json;

import java.io.StringWriter;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

import tools.datasync.api.utils.Stringify;
import tools.datasync.utils.ObjectMapperFactory;

public class Jsonify implements Stringify {

    private ObjectMapper flatMapper = ObjectMapperFactory.getInstance();
    private ObjectMapper prettyMapper = new ObjectMapper();

    public Jsonify() {
	prettyMapper
		.configure(
			DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,
			false);
	prettyMapper
		.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
	prettyMapper.disable(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS);
	prettyMapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);
    }

    public String toString(Object item) {
	return toStringSerialize(item);
    }

    public String toStringSerialize(Object item) {
	return (toString(flatMapper, item));
    }

    private String toString(ObjectMapper thisMapper, Object item) {
	StringWriter writer = new StringWriter();
	try {
	    thisMapper.writeValue(writer, item);
	} catch (Exception e) {
	    throw (new RuntimeException(e));
	}
	return writer.toString();

    }

    public String toStringPretty(Object item) {
	return (toString(prettyMapper, item));

    }

}
