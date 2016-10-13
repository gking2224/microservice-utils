package me.gking2224.common.client;

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

    public ErrorResponseException(int errorCode, String message) {
        this(new ErrorResponse(errorCode, message));
    }

    public ErrorResponse getResponse() {
        return response;
    }

    public void setResponse(ErrorResponse response) {
        this.response = response;
    }

}
