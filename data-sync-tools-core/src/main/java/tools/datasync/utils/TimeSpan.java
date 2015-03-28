package tools.datasync.utils;

import java.util.concurrent.TimeUnit;

public class TimeSpan {

    private long duration;
    private TimeUnit unit;

    public TimeSpan(long duration, TimeUnit unit) {
	super();
	this.duration = duration;
	this.unit = unit;
    }

    public long getDuration() {
	return duration;
    }

    public TimeUnit getUnit() {
	return unit;
    }

}
