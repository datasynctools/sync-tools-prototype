package tools.datasync.model.convert;

public class IntegerConverter implements Converter {

    public Object convert(String value) {
	return Integer.valueOf(value);
    }

}
