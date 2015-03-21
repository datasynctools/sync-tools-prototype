package tools.datasync.core.sampleapp;

import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

public final class ContextFilter extends org.apache.log4j.spi.Filter {

    private String threadRegExp;
    private String classRegExp;
    private Pattern acceptThreadPattern;
    private Pattern ignoreClassPattern = null;

    private Level level = Level.FATAL;

    public int decide(LoggingEvent event) {

	if (acceptThreadPattern.matcher(event.getThreadName()).matches()) {
	    if (event.getLevel().isGreaterOrEqual(level)) {
		if (ignoreClassPattern != null
			&& ignoreClassPattern.matcher(
				event.getLocationInformation().getClassName())
				.matches()) {
		    return (DENY);
		}
		return (NEUTRAL);
	    }
	}
	return (DENY);

	// Object ctx = event.getMDC(key);
	// if (value == null)
	// return (ctx != null) ? NEUTRAL : DENY;
	// else
	// return value.equals(ctx) ? NEUTRAL : DENY;
    }

    // public void setContextKey(String key) {
    // this.key = key;
    // }
    //
    // public String getContextKey() {
    // return key;
    // }
    //
    // public void setValue(String value) {
    // this.value = value;
    // }

    public void setAcceptThreadRegExp(String acceptThreadRegExp) {
	this.threadRegExp = acceptThreadRegExp;
	acceptThreadPattern = Pattern.compile(acceptThreadRegExp);
    }

    public String getAcceptThreadRegExp() {
	return (threadRegExp);
    }

    public void setIgnoreClassRegExp(String classRegExp) {
	this.classRegExp = classRegExp;
	ignoreClassPattern = Pattern.compile(classRegExp);
    }

    public String getClassRegExp() {
	return (classRegExp);
    }

    public void setLevel(String level) {
	this.level = Level.toLevel(level);
    }

    public String getLevel() {
	return (Integer.toString(level.toInt()));
    }

    // public String getValue() {
    // return value;
    // }

}