package me.gking2224.common.client;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ErrorResponse {

    protected HttpStatus status;
    protected String errorMessage;

    public ErrorResponse(final HttpStatus status, final String errorMessage) {
        this.status = status;
        this.errorMessage = errorMessage;
    }

    public ErrorResponse() {
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return status.value();
    }

    public void setErrorCode(int code) {
        this.status = HttpStatus.valueOf(code);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @JsonIgnore
    public HttpStatus getStatus() {
        return status;
    }
}
