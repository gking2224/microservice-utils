package me.gking2224.common.batch;

import static java.lang.String.format;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.batch.item.file.FlatFileParseException;

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
