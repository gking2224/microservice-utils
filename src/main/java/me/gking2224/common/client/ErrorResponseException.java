package me.gking2224.common.client;

import org.springframework.http.HttpStatus;

public class ErrorResponseException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 517825546308699831L;
    
    private ErrorResponse response;
    
    public ErrorResponseException(ErrorResponse response) {
        super();
        this.response = response;
    }

    public ErrorResponseException(final HttpStatus status, String message) {
        this(new ErrorResponse(status, message));
    }

    public ErrorResponse getResponse() {
        return response;
    }

    public void setResponse(ErrorResponse response) {
        this.response = response;
    }

}
