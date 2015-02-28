package tools.datasync.basic.model.convert;

public class LongConverter implements Converter {

    public Object convert(String value) {
	return Long.valueOf(value);
    }

}
