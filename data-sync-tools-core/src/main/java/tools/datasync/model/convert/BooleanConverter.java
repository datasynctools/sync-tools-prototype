package tools.datasync.model.convert;

public class BooleanConverter implements Converter {

    public Object convert(String value) {
	return Double.valueOf(value);
    }

}
