package me.gking2224.common.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import me.gking2224.common.client.ErrorResponse;

@ControllerAdvice
@Profile("web")
public class ControllerExceptionAdvice {
    
    @SuppressWarnings("unused")
    private static Logger logger = LoggerFactory.getLogger(ControllerExceptionAdvice.class);

    public ControllerExceptionAdvice() {
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> exceptionHandler(DataIntegrityViolationException ex) throws Exception {
        return errorResponse(HttpStatus.CONFLICT, "Data integrity violation");
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> exceptionHandler(DataAccessException ex) throws Exception {
        return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Data integrity violation");
    }

    private ResponseEntity<ErrorResponse> errorResponse(final HttpStatus status, final String description) {
    
        return new ResponseEntity<ErrorResponse>(new ErrorResponse(status, description), status);
    }
}
