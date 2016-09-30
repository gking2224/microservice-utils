package me.gking2224.common.batch;

import org.springframework.batch.core.step.builder.AbstractTaskletStepBuilder;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;

public interface StepConfigurer {


    <I, O> AbstractTaskletStepBuilder<SimpleStepBuilder<I, O>> configure(
            AbstractTaskletStepBuilder<SimpleStepBuilder<I, O>> builder);
}
