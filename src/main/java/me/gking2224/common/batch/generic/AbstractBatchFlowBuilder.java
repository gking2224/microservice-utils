package me.gking2224.common.batch.generic;

import java.util.Properties;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.Flow;

public abstract class AbstractBatchFlowBuilder extends AbstractBatchBuilder {

    public AbstractBatchFlowBuilder(
            final StepBuilderFactory steps,
            final Properties parentProperties,
            final String flowName
    ) {
        super(steps, parentProperties, flowName);
    }

    public Step createFlowStep(Flow flow) {
        
        return getSteps().get(getFlowStepName())
                .flow(flow)
                .build();
    }

    protected String getFlowStepName() {
        return getFlowName()+"Flow";
    }
}
