package me.gking2224.common.batch;

import java.util.Properties;

import org.springframework.batch.core.step.builder.AbstractTaskletStepBuilder;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.repeat.exception.ExceptionHandler;

public class SimpleStepConfigurer implements StepConfigurer{

    private ExceptionHandler exceptionHandler;
    
    @SuppressWarnings("unused")
    private Properties properties;

    public SimpleStepConfigurer() {
    }
    
    public SimpleStepConfigurer properties(final Properties props) {
        this.properties = props;
        return this;
    }
    
    public SimpleStepConfigurer exceptionHandler(final ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    @Override
    public <I, O> AbstractTaskletStepBuilder<SimpleStepBuilder<I, O>> configure(
            AbstractTaskletStepBuilder<SimpleStepBuilder<I, O>> builder
    ) {
        AbstractTaskletStepBuilder<SimpleStepBuilder<I, O>> rv = builder;
        if (this.exceptionHandler != null) {
            rv = rv.exceptionHandler(this.exceptionHandler);
        }
        return rv;
    }

}