package me.gking2224.common.batch;

import org.springframework.batch.core.step.builder.FaultTolerantStepBuilder;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;

public interface FaultToleranceConfigurer {

    <I, O> FaultTolerantStepBuilder<I, O> configure(SimpleStepBuilder<I, O> builder);
}
