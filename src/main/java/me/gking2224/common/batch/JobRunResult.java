package me.gking2224.common.batch;

import org.springframework.batch.core.JobExecution;

public class JobRunResult {

    String statusMessage;
    Throwable error;
    JobExecution executionResult;
    
    public String getStatusMessage() {
        return statusMessage;
    }
    public void setStatusMessage(String errorMessage) {
        this.statusMessage = errorMessage;
    }
    public Throwable getError() {
        return error;
    }
    public void setError(Throwable error) {
        this.error = error;
    }
    public JobExecution getExecutionResult() {
        return executionResult;
    }
    public void setExecutionResult(JobExecution executionResult) {
        this.executionResult = executionResult;
    }
}
