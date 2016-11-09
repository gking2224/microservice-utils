package me.gking2224.common.batch;

import static me.gking2224.common.utils.PropertyResolverUtils.getDouble;
import static me.gking2224.common.utils.PropertyResolverUtils.getString;

import org.springframework.core.env.PropertyResolver;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.backoff.ThreadWaitSleeper;

import me.gking2224.common.utils.DurationFormatter;

public class BackOffPolicyBuilder {
    
    private static DurationFormatter df = DurationFormatter.getInstance();
    
    private String initialInterval;
    private Double multiplier;
    private String maxInterval;

    private PropertyResolver properties;
    
    public BackOffPolicyBuilder() {
    }
    
    public BackOffPolicyBuilder properties(PropertyResolver properties) {
        this.properties = properties;
        return this;
    }

    public BackOffPolicy build() {

        if (initialInterval == null)
            initialInterval = getString(properties, "backOffPolicy.initialInterval", "2s");
        
        if (multiplier == null)
            multiplier = getDouble(properties, "backOffPolicy.multiplier", 1.2d);
        
        if (maxInterval == null)
            maxInterval = getString(properties, "backOffPolicy.maxInterval", "1h");
        
        
        ExponentialBackOffPolicy policy = new ExponentialBackOffPolicy();
        policy.setSleeper(new LoggingSleeperWrapper(new ThreadWaitSleeper()));
        policy.setInitialInterval(df.apply(initialInterval).toMillis());
        policy.setMultiplier(multiplier);
        policy.setMaxInterval(df.apply(maxInterval).toMillis());
        return policy;
    }
}
