package tools.datasync.model.convert;

import java.util.Date;

public class DateConverter implements Converter {

    public Object convert(String value) {
	return new Date(Long.valueOf(value));
    }

}
