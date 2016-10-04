package me.gking2224.common.batch.generic;

import static me.gking2224.common.batch.BatchConstants.BATCH_DATE;

import java.util.Optional;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;

import me.gking2224.common.utils.ObjectUtils;

public class StepExecutionHolder implements StepExecutionListener {

    private ThreadLocal<StepExecution> stepExecution = new ThreadLocal<StepExecution>();

    protected final StepExecution getStepExecution() {
        return stepExecution.get();
    }
    protected final ExecutionContext getJobContext() {
        return getJobExecution().getExecutionContext();
    }
    protected final ExecutionContext getStepContext() {
        return getStepExecution().getExecutionContext();
    }
    protected final JobExecution getJobExecution() {
        return getStepExecution().getJobExecution();
    }
    
    protected final JobParameters getJobParameters() {
        return getStepExecution().getJobExecution().getJobParameters();
    }
    
    protected final <T> Optional<T> getFromJobContext(final String key, final Class<T> type) {
        return getFromContext(key, type, getJobContext());
    }
    
    protected final <T> Optional<T> getFromStepContext(final String key, final Class<T> type) {
        return getFromContext(key, type, getStepContext());
    }
    
    protected final Optional<Object> getFromJobContext(final String key) {
        return getFromContext(key, Object.class, getJobContext());
    }
    
    protected final Optional<Object> getFromStepContext(final String key) {
        return getFromContext(key, Object.class, getStepContext());
    }
    
    private final <T> Optional<T> getFromContext(final String key, final Class<T> type, final ExecutionContext context) {
        return ObjectUtils.castObject(context.get(key), type);
    }
    
    protected final <T> T getFromJobContext(final String key, final Class<T> type, T defaultValue) {
        return getFromContext(key, type, defaultValue, getJobContext());
    }
    
    protected final <T> T getFromStepContext(final String key, final Class<T> type, final T defaultValue) {
        return getFromContext(key, type, defaultValue, getStepContext());
    }
    
    protected final Object getFromJobContext(final String key, final Object defaultValue) {
        return getFromContext(key, Object.class, defaultValue, getJobContext());
    }
    
    protected final Object getFromStepContext(final String key, final Object defaultValue) {
        return getFromContext(key, Object.class, defaultValue, getStepContext());
    }
    
    private final <T> T getFromContext(final String key, final Class<T> type, final T defaultValue, final ExecutionContext context) {
        return ObjectUtils.castObject(context.get(key), type).orElse(defaultValue);
    }
    
    protected final void putInJobContext(final String key, final Object value) {
        putInContext(key, value, getJobContext());
    }
    
    protected final void setInStepContext(final String key, final Object value) {
        putInContext(key, value, getStepContext());
    }
    
    private final void putInContext(final String key, final Object value, final ExecutionContext context) {
        context.put(key, value);
    }
    
    protected String getBatchDate() {
        return getJobParameters().getString(BATCH_DATE);
    }
    
    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution.set(stepExecution);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return ExitStatus.COMPLETED;
    }

}
