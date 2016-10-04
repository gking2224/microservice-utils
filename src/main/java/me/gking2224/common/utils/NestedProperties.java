package me.gking2224.common.utils;

import static java.lang.String.format;

import java.util.Properties;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NestedProperties extends Properties {
    
    private static Logger logger = LoggerFactory.getLogger(NestedProperties.class);

    /**
     * 
     */
    private static final long serialVersionUID = 8863605320894985731L;

    private Properties wrapped;
    
    private String prefix;

    public NestedProperties(final String prefix, final Properties wrapped) {
        super(wrapped);
        this.prefix = prefix;
        this.wrapped = wrapped;
    }
    
    @Override public Object get(final Object key) {
        return getOrDefault(key, null);
    }
    
    @Override public Object getOrDefault(Object key, Object defaultValue) {
        return doWithKeyOrDefault(key, defaultValue, (k)-> wrapped.get(k));
    }
    
    @Override public String getProperty(String key) {
        return getProperty(key, null);
    }
    
    @Override public String getProperty(String key, String defaultValue) {
        return (String)doWithKeyOrDefault(key, defaultValue, (k)-> wrapped.getProperty((String)k));
    }

    private String prefix(final String key) {
        return format("%s.%s", prefix, key);
    }
    
    private Object doWithKeyOrDefault(final Object initialKey, Object defaultValue, Function<Object,Object> func) {
        Object value = doWithKey(initialKey, func);
        if (value != null) return value;
        else return defaultValue;
    }
    
    private Object doWithKey(final Object initialKey, Function<Object,Object> func) {
        if (initialKey == null) return null;
        Object key = prefix(initialKey.toString());
        Object value = func.apply(key);
        if (value != null && !NestedProperties.class.isAssignableFrom(wrapped.getClass())) {
            logger.debug("Using {}={}", key, value);
        }
        if (value == null) {
            key = initialKey;
            value = func.apply(key);
            if (value != null && !NestedProperties.class.isAssignableFrom(wrapped.getClass())) {
                logger.debug("Using {}={}", key, value);
            }
        }
        return value;
    }
}
