package me.gking2224.common.batch;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static me.gking2224.common.utils.PropertyResolverUtils.getInteger;
import static me.gking2224.common.utils.PropertyResolverUtils.getString;
import static org.springframework.util.StringUtils.commaDelimitedListToSet;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import org.springframework.batch.core.step.skip.AlwaysSkipItemSkipPolicy;
import org.springframework.batch.core.step.skip.LimitCheckingItemSkipPolicy;
import org.springframework.batch.core.step.skip.NeverSkipItemSkipPolicy;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.core.env.PropertyResolver;


public class SkipPolicyBuilder {
    
    public enum SkipPolicyType {
        NEVER, ALWAYS, LIMITED
    }

    private static final Integer DEFAULT_LIMIT = 20;

    private SkipPolicyType type;
    private PropertyResolver properties;

    private Integer limit;
    
    public SkipPolicyBuilder() {
    }
    
    public SkipPolicyBuilder properties(final PropertyResolver properties) {
        this.properties = properties;
        return this;
    }
    
    public SkipPolicyBuilder type(final SkipPolicyType type) {
        this.type = type;
        return this;
    }

    public SkipPolicy build() {

        if (type == null) type = SkipPolicyType.valueOf(getString(properties, "skipPolicy.type", SkipPolicyType.LIMITED.toString()));
        
        switch (type) {
        case NEVER:
            return never();
        case ALWAYS:
            return always();
        case LIMITED:
        default:
            return limited();
        }
    }
    
    protected SkipPolicy never() {
        return new NeverSkipItemSkipPolicy();
    }
    
    protected SkipPolicy always() {
        return new AlwaysSkipItemSkipPolicy();
    }
    
    protected SkipPolicy limited() {
        if (this.limit == null) limit = getInteger(properties, "skipPolicy.limited.skipLimit", DEFAULT_LIMIT);

        Set<String> skippableClasses = commaDelimitedListToSet(getString(properties, "skipPolicy.limited.skipOn", "java.lang.Throwable"));
        Set<String> nonSkippableClasses = commaDelimitedListToSet(getString(properties, "skipPolicy.limited.doNotSkipOn", ""));
        
        Map<Class<? extends Throwable>, Boolean> exceptionMap = new HashMap<Class<? extends Throwable>, Boolean>();
        skippableClasses.stream().map(toMapEntry(TRUE)).forEach(populateMap(exceptionMap));
        nonSkippableClasses.stream().map(toMapEntry(FALSE)).forEach(populateMap(exceptionMap));
        
        LimitCheckingItemSkipPolicy policy = new LimitCheckingItemSkipPolicy(limit, exceptionMap);
        return policy;
    }
    
    private Consumer<Entry<Class<? extends Throwable>, Boolean>> populateMap(Map<Class<? extends Throwable>, Boolean> exceptionMap) {
        return (Entry<Class<? extends Throwable>, Boolean> e) -> {
            if (e != null) exceptionMap.put(e.getKey(), e.getValue());
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
