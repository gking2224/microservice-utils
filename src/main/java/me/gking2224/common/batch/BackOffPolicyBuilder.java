package me.gking2224.common.batch;

import java.util.Properties;

import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.backoff.ThreadWaitSleeper;

import me.gking2224.common.utils.DurationFormatter;
import me.gking2224.common.utils.PropertyUtils;

public class BackOffPolicyBuilder {
    
    private static DurationFormatter df = DurationFormatter.getInstance();
    
    private String initialInterval;
    private Double multiplier;
    private String maxInterval;

    private Properties properties;
    
    public BackOffPolicyBuilder() {
    }
    
    public BackOffPolicyBuilder properties(Properties props) {
        this.properties = props;
        return this;
    }

    public BackOffPolicy build() {

        if (initialInterval == null)
            initialInterval = PropertyUtils.getString(properties, "backOffPolicy.initialInterval", "2s");
        
        if (multiplier == null)
            multiplier = PropertyUtils.getDouble(properties, "backOffPolicy.multiplier", 1.2d);
        
        if (maxInterval == null)
            maxInterval = PropertyUtils.getString(properties, "backOffPolicy.maxInterval", "1h");
        
        
        ExponentialBackOffPolicy policy = new ExponentialBackOffPolicy();
        policy.setSleeper(new LoggingSleeperWrapper(new ThreadWaitSleeper()));
        policy.setInitialInterval(df.apply(initialInterval).toMillis());
        policy.setMultiplier(multiplier);
        policy.setMaxInterval(df.apply(maxInterval).toMillis());
        return policy;
    }
}
