package me.gking2224.common.batch.generic;

import java.io.File;
import java.util.Properties;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertyResolver;

import me.gking2224.common.batch.step.CheckForFileTaskletBuilder;

public class CheckFileExistsStepBuilder extends AbstractBatchStepBuilder<CheckFileExistsStepBuilder> {
    
    private static Logger logger = LoggerFactory.getLogger(CheckFileExistsStepBuilder.class);
    
    private Function<StepExecutionHolder, File> fileProvider;
    
    public CheckFileExistsStepBuilder(
            final StepBuilderFactory steps,
            final ConfigurableEnvironment environment,
            final Properties parentProperties,
            final String flowName,
            final String stepName
    ) {
        super(steps, environment, parentProperties, flowName, stepName);
    }

    public CheckFileExistsStepBuilder(StepBuilderFactory steps, PropertyResolver properties, String flowName, String stepName) {
        super(steps, properties, flowName, stepName);
    }

    public CheckFileExistsStepBuilder fileProvider(final Function<StepExecutionHolder, File> fileProvider) {
        this.fileProvider = fileProvider;
        return this;
    }
    
    protected Step build() {
        return stepBuilder()
                .tasklet(tasklet())
                .build();
    }

    private Tasklet tasklet() {
        return new CheckForFileTaskletBuilder()
                .contextSupplier(this::getStepExecutionHolder)
                .file(() -> fileProvider.apply(getStepExecutionHolder()))
                .properties(getProperties()).build();
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
