package tools.datasync.utils;

public class StringUtils {

    public static boolean isEmpty(String str) {
	return (str == null || "".equals(str));
    }

    public static boolean isEmpty(Object str) {
	return (str == null || "".equals(str));
    }

    public static boolean isNotEmpty(String str) {
	return (str != null && str.length() > 0);
    }

    public static boolean isWhiteSpaceOnly(String str) {
	boolean result = false;
	if (isEmpty(str)) {
	    result = true;
	} else {
	    str = str.replace('\n', ' ');
	    str = str.replace('\t', ' ');
	    str = str.replace('\b', ' ');
	    str = str.trim();
	    if (isEmpty(str)) {
		result = true;
	    }
	}
	return result;
    }

    public static String getSimpleName(Object obj) {
	return (obj.getClass().getSimpleName());
    }

}
