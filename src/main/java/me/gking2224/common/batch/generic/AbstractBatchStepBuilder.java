package me.gking2224.common.batch.generic;

import java.util.Properties;

import org.slf4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.builder.StepBuilder;

import me.gking2224.common.batch.FaultToleranceConfigurer;
import me.gking2224.common.batch.SimpleFaultToleranceConfigurer;

public abstract class AbstractBatchStepBuilder<T> extends AbstractBatchBuilder
implements StepExecutionListener {
    
    private FaultToleranceConfigurer faultToleranceConfigurer = new SimpleFaultToleranceConfigurer(getProperties());
    private String stepName;
    private Object stepExecutionListener;
    private StepExecutionHolder stepExecutionHolder = new StepExecutionHolder();

    public AbstractBatchStepBuilder(
            final StepBuilderFactory steps,
            final Properties parentProperties,
            final String flowName, String stepName
    ) {
        super(steps, parentProperties, flowName);
        this.stepName = stepName;
    }

    protected final FaultToleranceConfigurer getFaultToleranceConfigurer() {
        return faultToleranceConfigurer;
    }

    protected final String getStepName() {
        return stepName;
    }

    protected String getFullName() {
        return getFlowName()+"."+getStepName();
    }

    protected final StepBuilder stepBuilder() {
        StepBuilder builder = getSteps().get(getFullName());
        builder = builder.listener(stepExecutionHolder);
        builder.listener((StepExecutionListener)this);
        if (this.stepExecutionListener != null) {
            builder = builder.listener(this.stepExecutionListener);
        }
        
        return builder;
    }

    protected final StepExecutionHolder getStepExecutionHolder() {
        return stepExecutionHolder;
    }

    @SuppressWarnings("unchecked")
    protected final T listener(Object listener) {
        this.stepExecutionListener = listener;
        return (T)this;
    }
    
    protected abstract Logger getLogger();

    @Override
    public final void beforeStep(StepExecution stepExecution) {
        getLogger().debug("{}: before step: {}", getFullName(), stepExecution);
        doBeforeStep(stepExecution);
    }
    protected void doBeforeStep(StepExecution stepExecution) {}

    @Override
    public final ExitStatus afterStep(StepExecution stepExecution) {
        getLogger().debug("{}: after step: {}", getFullName(), stepExecution);
        return doAfterStep(stepExecution);
    }
    protected ExitStatus doAfterStep(StepExecution stepExecution) {
        return ExitStatus.COMPLETED;
    }
}
