package tools.datasync.api.utils;

/**
 * While this could be used for many purposes, this class exists to be able to
 * create a consistent string value
 * 
 * @author doug
 *
 * @param <T>
 */
public interface Stringify {
    String toString(Object item);
}
