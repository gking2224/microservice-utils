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

import me.gking2224.common.batch.step.MoveFileTaskletBuilder;

public class MoveFileStepBuilder extends AbstractBatchStepBuilder<MoveFileStepBuilder> {
    
    private static Logger logger = LoggerFactory.getLogger(MoveFileStepBuilder.class);

    private String suffix = "processed";
    private Function<StepExecutionHolder, File> fileProvider;

    public MoveFileStepBuilder(
            final StepBuilderFactory steps,
            final ConfigurableEnvironment environment,
            final Properties parentProperties,
            final String flowName,
            final String stepName
    ) {
        super(steps, environment, parentProperties, flowName, stepName);
    }

    public MoveFileStepBuilder(
            final StepBuilderFactory steps,
            final PropertyResolver properties,
            final String flowName, final String stepName) {
        super(steps, properties, flowName, stepName);
    }
    
    public MoveFileStepBuilder suffix(final String suffix) {
        this.suffix = suffix;
        return this;
    }
    
    public Step build() {

        Tasklet t = new MoveFileTaskletBuilder()
                .file(() -> fileProvider.apply(getStepExecutionHolder()))
                .addSuffix(this.suffix)
                .build();
        
        return stepBuilder().tasklet(t).build();
    }

    public MoveFileStepBuilder fileProvider(final Function<StepExecutionHolder, File> fileProvider) {
        this.fileProvider = fileProvider;
        return this;
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
