package me.gking2224.common.batch;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static me.gking2224.common.utils.PropertyUtils.getString;
import static org.springframework.util.StringUtils.commaDelimitedListToSet;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import org.springframework.retry.RetryPolicy;
import org.springframework.retry.policy.AlwaysRetryPolicy;
import org.springframework.retry.policy.CompositeRetryPolicy;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.policy.TimeoutRetryPolicy;

import me.gking2224.common.utils.DurationFormatter;
import me.gking2224.common.utils.PropertyUtils;

public class RetryPolicyBuilder {
    
    public enum RetryPolicyType {
        NEVER, ALWAYS, SIMPLE, TIMEOUT, FIXED_TIME
    }

    private static final String DEFAULT_TIMEOUT = "1h";

    private static final String DEFAULT_TIME = "15:00:00";

    private static final String DEFAULT_ZONE_ID = "GMT";

    private static final int DEFAULT_ATTEMPTS = 999;
    
    private static DurationFormatter df = DurationFormatter.getInstance();
    private static DateTimeFormatter timeFormat = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(Locale.UK);
    
    private RetryPolicyType type;
    private Properties properties;

    private Duration timeout;

    private LocalTime time;

    private ZoneId timezone;

    private Integer attempts;
    
    public RetryPolicyBuilder() {
    }
    
    public RetryPolicyBuilder properties(final Properties properties) {
        this.properties = properties;
        return this;
    }
    
    public RetryPolicyBuilder type(final RetryPolicyType type) {
        this.type = type;
        return this;
    }

    public RetryPolicy build() {

        if (type == null) type = RetryPolicyType.valueOf(PropertyUtils.getString(properties, "retryPolicy.type", RetryPolicyType.SIMPLE.toString()));
        
        switch (type) {
        case NEVER:
            return never();
        case ALWAYS:
            return always();
        case TIMEOUT:
            return timeout();
        case FIXED_TIME:
            return fixedTime();
        case SIMPLE:
        default:
            return simple();
        }
    }
    
    protected RetryPolicy never() {
        return new NeverRetryPolicy();
    }
    
    protected RetryPolicy always() {
        return new AlwaysRetryPolicy();
    }
    
    protected RetryPolicy timeout() {
        if (this.timeout == null) timeout = df.apply(PropertyUtils.getString(properties, "retryPolicy.timeout", DEFAULT_TIMEOUT));
        
        TimeoutRetryPolicy torp = new TimeoutRetryPolicy();
        torp.setTimeout(timeout.toMillis());
        
        return getComposite(torp);
    }
    
    private RetryPolicy getComposite(RetryPolicy policy) {
        CompositeRetryPolicy composite = new CompositeRetryPolicy();
        composite.setPolicies(new RetryPolicy[] { simple(), policy });
        return composite;
    }

    protected RetryPolicy fixedTime() {
        if (this.timezone == null) timezone = ZoneId.of(PropertyUtils.getString(properties, "retryPolicy.fixedTime.timezone", DEFAULT_ZONE_ID));
        if (this.time == null) time = LocalTime.from(timeFormat.parse(PropertyUtils.getString(properties, "retryPolicy.fixedTime.time", DEFAULT_TIME)));
        
        final LocalDateTime time = LocalDateTime.of(LocalDate.now(), this.time);
        
        return getComposite(new FixedTimeRetryPolicy(time.atZone(timezone)));
    }
    
    protected RetryPolicy simple() {
        if (this.attempts == null) attempts = PropertyUtils.getInteger(properties, "retryPolicy.simple.attempts", DEFAULT_ATTEMPTS);
        Set<String> retryClasses = commaDelimitedListToSet(getString(properties, "retryPolicy.simple.retryOn", "java.lang.Throwable"));
        Set<String> doNotRetryClasses = commaDelimitedListToSet(getString(properties, "retryPolicy.simple.doNotRetryOn", ""));
        
        Map<Class<? extends Throwable>, Boolean> exceptionMap = new HashMap<Class<? extends Throwable>, Boolean>();
        retryClasses.stream().map(toMapEntry(TRUE)).forEach(populateMap(exceptionMap));
        doNotRetryClasses.stream().map(toMapEntry(FALSE)).forEach(populateMap(exceptionMap));
                
        return new SimpleRetryPolicy(this.attempts, exceptionMap);
    }

    private Consumer<Entry<Class<? extends Throwable>, Boolean>> populateMap(Map<Class<? extends Throwable>, Boolean> exceptionMap) {
        return (Entry<Class<? extends Throwable>, Boolean> e) -> {
            if (e != null && e.getKey() != null) exceptionMap.put(e.getKey(), e.getValue());
        };
    }

    private Function<? super String, ? extends SimpleEntry<Class<? extends Throwable>, Boolean>> toMapEntry(Boolean retry) {
        return (s) -> new AbstractMap.SimpleEntry<Class<? extends Throwable>, Boolean>(toClass(s), retry);
    }

    @SuppressWarnings("unchecked")
    private Class<? extends Throwable> toClass(String s) {
        try {
            return (Class<? extends Throwable>)Class.forName(s);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
