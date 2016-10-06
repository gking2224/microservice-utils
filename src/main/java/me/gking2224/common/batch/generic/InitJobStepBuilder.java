package me.gking2224.common.batch.generic;

import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;

public class InitJobStepBuilder extends AbstractBatchStepBuilder<InitJobStepBuilder> {
    
    private static Logger logger = LoggerFactory.getLogger(InitJobStepBuilder.class);
    
    private Map<String, Object> contextAttributes;
    
    public InitJobStepBuilder(
            final StepBuilderFactory steps,
            final Properties parentProperties,
            final String flowName,
            final String stepName
    ) {
        super(steps, parentProperties, flowName, stepName);
    }
    
    public InitJobStepBuilder params(final Map<String, Object> contextAttributes) {
        this.contextAttributes = contextAttributes;
        return this;
    }
    
    protected Step build() {
        return stepBuilder()
                .tasklet(tasklet())
                .build();
    }
    
    public InitJobStepBuilder contextParams(Map<String,Object> contextAttributes) {
        this.contextAttributes = contextAttributes;
        return this;
    }
    
    protected Tasklet tasklet() {

        return new InitExecutionContextTaskletBuilder()
                .contextSupplier(this::getStepExecutionHolder)
                .contextAttributes(contextAttributes)
                .build();
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

}
