package me.gking2224.common.batch.step;

import static java.lang.String.format;
import static me.gking2224.common.batch.BatchConstants.BATCH_DATE;
import static org.apache.commons.lang.exception.ExceptionUtils.getRootCauseMessage;
import static org.springframework.batch.core.ExitStatus.COMPLETED;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.FatalStepExecutionException;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.exception.ExceptionHandler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;

import me.gking2224.common.batch.FaultToleranceConfigurer;
import me.gking2224.common.batch.SimpleFaultToleranceConfigurer;
import me.gking2224.common.utils.NestedProperties;
import me.gking2224.common.utils.ObjectUtils;

@Profile("batch")
public abstract class AbstractStep implements InitializingBean, ExceptionHandler {

    private static Logger logger = LoggerFactory.getLogger(AbstractStep.class);

    @Autowired @Qualifier("batchProperties")
    private Properties batchProperties;
    
    protected Logger getLogger() {
        return logger;
    }
    
    protected final Supplier<RuntimeException> notInitialized(final String key) {
        return () -> new FatalStepExecutionException(format("%s not initialized", key), null);
    }

    @Autowired
    protected StepBuilderFactory steps;

    private ThreadLocal<StepExecution> stepExecution = new ThreadLocal<StepExecution>();
    
    private FaultToleranceConfigurer faultToleranceConfigurer;
    
    protected FaultToleranceConfigurer getFaultToleranceConfigurer() {
        return faultToleranceConfigurer;
    }
    
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
    protected <T> List<T> initializeListInContext(final String key, final ExecutionContext context, final Class<T> clazz) {
        @SuppressWarnings("unchecked")
        List<T> list = (List<T>)context.get(key);
        if (list == null) {
            list = new ArrayList<T>();
            context.put(key, list);
        }
        return list;
    }
    
    private Properties properties;

    protected final Properties getProperties() {
        return this.properties;
    }
    protected Properties getJobProperties() {
        return null;
    }
    protected String getStepName() {
        return null;
    }
    
    @Override
    public final void afterPropertiesSet() throws Exception {
        initProperties();
        if (this.properties != null) {
            faultToleranceConfigurer = new SimpleFaultToleranceConfigurer(this.properties);
        }
        doAfterPropertiesSet();
    }
    private void initProperties() {
        Properties jobProperties = getJobProperties();
        String stepName = getStepName();
        if (stepName == null && jobProperties == null) {
            getLogger().debug("stepName and jobProperties not defined, using batchProperties");
            this.properties = batchProperties;
        }
        else if (stepName == null && jobProperties != null) {
            getLogger().debug("stepName not defined, using jobProperties");
            this.properties = jobProperties;
        }
        else if (stepName != null && jobProperties != null) {
            this.properties = new NestedProperties(getStepName(), getJobProperties());
        }
    }

    protected void doAfterPropertiesSet() throws Exception {}
    
    
    // STEP LIFECYCLE
    @BeforeStep
    public final void beforeStep(StepExecution stepExecution) {
        getLogger().debug("before step");
        this.stepExecution.set(stepExecution);
        doBeforeStep();
    }
    protected void doBeforeStep() {}
    
    @AfterStep
    public final ExitStatus afterStep(StepExecution stepExecution) {
        getLogger().debug("after step");
        return doAfterStep(stepExecution);
    }
    
    protected ExitStatus doAfterStep(StepExecution stepExecution) {
        return COMPLETED;
    }

    
    // EXCEPTION HANDLER
    @Override
    public final void handleException(RepeatContext context, Throwable throwable) throws Throwable {
        getLogger().error("handle exception {}: {}", context, getRootCauseMessage(throwable));
        getStepExecution().addFailureException(throwable);
        doHandleException(context, throwable);
    }
    protected void doHandleException(RepeatContext context, Throwable throwable) throws Throwable {}
}
