package tools.datasync.basic.util;

public class StringUtils {

    public static boolean isEmpty(String str){
        return (str == null || "".equals(str));
    }
    
    public static boolean isNotEmpty(String str){
        return (str != null && str.length() > 0);
    }
}
