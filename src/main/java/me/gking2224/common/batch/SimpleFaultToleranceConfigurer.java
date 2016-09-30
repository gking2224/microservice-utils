package me.gking2224.common.batch;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.step.builder.FaultTolerantStepBuilder;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.retry.RetryPolicy;

public class SimpleFaultToleranceConfigurer implements FaultToleranceConfigurer {

    private static Logger logger = LoggerFactory.getLogger(SimpleFaultToleranceConfigurer.class);

    private Properties properties;
    
    private RetryPolicy retryPolicy;
    private SkipPolicy skipPolicy;
    
    public SimpleFaultToleranceConfigurer(final Properties properties) {
        this.properties = properties;
    }
    
    public SimpleFaultToleranceConfigurer skipPolicy(final SkipPolicy skipPolicy) {
        this.skipPolicy = skipPolicy;
        return this;
    }
    
    public SimpleFaultToleranceConfigurer retryPolicy(final RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
        return this;
    }

    public <I, O> FaultTolerantStepBuilder<I, O> configure(SimpleStepBuilder<I, O> builder) {
        
        if (skipPolicy == null) {
            skipPolicy = new SkipPolicyBuilder().properties(properties).build();
        }
        if (retryPolicy == null) {
            retryPolicy = new RetryPolicyBuilder().properties(properties).build();
        }
        
        logger.debug("Configuring step with skipPolicy{} and retryPolicy {}", skipPolicy, retryPolicy);
        
        FaultTolerantStepBuilder<I, O> ftBuilder = builder
                .faultTolerant()
                .skipPolicy(skipPolicy)
                .retryPolicy(retryPolicy);
        
        return ftBuilder;
    }
}