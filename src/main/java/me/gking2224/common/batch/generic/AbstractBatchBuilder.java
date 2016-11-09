package me.gking2224.common.batch.generic;

import static java.lang.String.format;

import java.io.File;
import java.util.Properties;
import java.util.function.Supplier;

import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.FatalStepExecutionException;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertyResolver;

import me.gking2224.common.client.PropertiesPropertySource;
import me.gking2224.common.utils.NestedProperties;

public abstract class AbstractBatchBuilder {

    private String flowName;
    private PropertyResolver propertyResolver;
    private StepBuilderFactory steps;
    
    public AbstractBatchBuilder(
            final StepBuilderFactory steps,
            final ConfigurableEnvironment environment,
            final Properties parentProperties,
            final String flowName
    ) {
        this.steps = steps;
        this.flowName = flowName;
        environment.getPropertySources().addFirst(
                new PropertiesPropertySource(getFlowName(), new NestedProperties(flowName, parentProperties)));
        this.propertyResolver = environment;
    }

    public AbstractBatchBuilder(StepBuilderFactory steps, PropertyResolver properties, String flowName,
            String stepName) {
        this.steps = steps;
        this.propertyResolver = properties;
        this.flowName = flowName;
    }

    protected final String getFlowName() {
        return flowName;
    }

    protected File getBatchFilesDir() {
        return new File(propertyResolver.getProperty("batch.baseDir"));
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
    
    protected final PropertyResolver getProperties() {
        return propertyResolver;
    }
}
