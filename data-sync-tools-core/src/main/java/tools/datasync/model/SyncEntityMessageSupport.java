package tools.datasync.model;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import tools.datasync.model.convert.BooleanConverter;
import tools.datasync.model.convert.Converter;
import tools.datasync.model.convert.DateConverter;
import tools.datasync.model.convert.DoubleConverter;
import tools.datasync.model.convert.IntegerConverter;
import tools.datasync.model.convert.LongConverter;
import tools.datasync.model.convert.StringConverter;

public class SyncEntityMessageSupport {

    private final static Map<String, Converter> converters = new HashMap<String, Converter>();

    private Map<String, Object> props;
    private Map<String, String> types;

    public SyncEntityMessageSupport() {
	// Linked hash map to keep the order.
	this.props = new LinkedHashMap<String, Object>();
	this.types = new LinkedHashMap<String, String>();

	setupConverters();
    }

    private void setupConverters() {
	if (converters.size() == 0) {
	    converters.put("string", new StringConverter());
	    converters.put("integer", new IntegerConverter());
	    converters.put("long", new LongConverter());
	    converters.put("double", new DoubleConverter());
	    converters.put("boolean", new BooleanConverter());
	    converters.put("date", new DateConverter());
	}
    }

    public void set(String name, Object value) {

	if (value == null || "".equals(value)) {
	    return;
	}
	if (value instanceof Date) {
	    value = ((Date) value).getTime();
	}
	this.props.put(name, value);
	this.types.put(name, value.getClass().getSimpleName());
    }

    public Object get(String name) {

	String type = this.types.get(name);
	String value = String.valueOf(this.props.get(name));

	Converter converter = converters.get(type.toLowerCase());

	if (converter != null) {
	    return converter.convert(value);
	}

	throw new IllegalArgumentException("Type not supported " + type
		+ " for value " + value + ", for name " + name);

    }

    public String getType(String name) {

	return types.get(name);
    }

    public Map<String, Object> getData() {

	return this.props;
    }

    public void setData(Map<String, Object> props) {

	this.props = props;
    }

    public Map<String, String> getTypes() {
	return types;
    }

    public void setTypes(Map<String, String> types) {
	this.types = types;
    }

}
