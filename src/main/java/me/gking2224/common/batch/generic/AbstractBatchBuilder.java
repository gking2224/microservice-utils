package me.gking2224.common.batch.generic;

import static java.lang.String.format;

import java.io.File;
import java.util.Properties;
import java.util.function.Supplier;

import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.FatalStepExecutionException;

import me.gking2224.common.utils.NestedProperties;

public abstract class AbstractBatchBuilder {

    private String flowName;
    private Properties properties;
    private StepBuilderFactory steps;
    
    public AbstractBatchBuilder(
            final StepBuilderFactory steps,
            final Properties parentProperties,
            final String flowName
    ) {
        this.steps = steps; 
        this.flowName = flowName;
        this.properties = new NestedProperties(flowName, parentProperties);
    }

    protected final String getFlowName() {
        return flowName;
    }

    protected final Properties getProperties() {
        return properties;
    }

    protected File getBatchFilesDir() {
        return new File(properties.getProperty("batch.baseDir"));
    }

    protected final StepBuilderFactory getSteps() {
        return steps;
    }
    
    protected final String getJobKey(String key) {
        return getFlowName()+"."+key;
    }
    
    protected final Supplier<RuntimeException> notInitialized(final String key) {
        return () -> new FatalStepExecutionException(format("%s: %s not initialized", getFullName(), key), null);
    }

    protected abstract String getFullName();
    
}
