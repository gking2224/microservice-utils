package me.gking2224.common.batch;

import static java.lang.String.format;

import org.springframework.batch.item.file.FlatFileParseException;

import me.gking2224.common.utils.ExceptionUtils;

public class BatchUtils {

    
    public static String exceptionToFileString(final Throwable t) {
        if (FlatFileParseException.class.isAssignableFrom(t.getClass())) {
            return format("Error parsing line '%s'", ((FlatFileParseException)t).getInput());
        }
        else {
            Throwable rootCause = ExceptionUtils.getRootCause(t);
            return format("Error: %s : %s", rootCause.getClass().getName(), rootCause.getMessage());
        }
    }
}
