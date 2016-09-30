package me.gking2224.common.utils;

public class ExeptionUtils {
    
    public static Throwable getRootCause(Throwable t) {
        Throwable cause = t.getCause();
        if (cause != null && cause != t) {
            return getRootCause(cause);
        }
        else {
            return t;
        }
    }
}
