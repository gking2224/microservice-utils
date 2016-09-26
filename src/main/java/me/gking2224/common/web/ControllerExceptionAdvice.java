package me.gking2224.common.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionAdvice {
    
    private Map<Class<? extends Exception>, String> messageMap =
            new HashMap<Class<? extends Exception>, String>();
    private Map<Class<? extends Exception>, HttpStatus> httpStatusMap =
            new HashMap<Class<? extends Exception>, HttpStatus>();
    
    public ControllerExceptionAdvice() {
        mapException(DataIntegrityViolationException.class, "Data integrity violation", HttpStatus.CONFLICT);
        mapException(DataAccessException.class, "Data access error", HttpStatus.INTERNAL_SERVER_ERROR);
        mapException(Exception.class, "Internal error", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @SuppressWarnings("unchecked")
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exceptionHandler(Exception ex) {
        ex.printStackTrace();
        ErrorResponse error = new ErrorResponse();
        HttpStatus httpStatus = null;
        Class<? extends Exception> clazz = ex.getClass();
        while (!httpStatusMap.containsKey(clazz)) {
            clazz = (Class<? extends Exception>) clazz.getSuperclass();
        }
        httpStatus = httpStatusMap.get(clazz);
        if (httpStatus == null) {
            httpStatus = HttpStatus.I_AM_A_TEAPOT; // this should not happen!
        }
        error.setErrorCode(httpStatus.value());
        error.setErrorMessage(messageMap.get(clazz));
        return new ResponseEntity<ErrorResponse>(error, httpStatus);
    }

    private void mapException(Class<? extends Exception> ex, String msg,
            HttpStatus httpStatus) {
        messageMap.put(ex, msg);
        httpStatusMap.put(ex, httpStatus);
    }
}
