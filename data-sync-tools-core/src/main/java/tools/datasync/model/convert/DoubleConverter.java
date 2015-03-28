package tools.datasync.model.convert;

public class DoubleConverter implements Converter {

    public Object convert(String value) {
	return Double.valueOf(value);
    }

}
