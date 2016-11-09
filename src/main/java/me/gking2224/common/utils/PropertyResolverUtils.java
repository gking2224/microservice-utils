package me.gking2224.common.utils;

import static java.util.Objects.isNull;
import static me.gking2224.common.utils.ObjectUtils.castObject;

import java.util.Optional;
import java.util.Properties;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.env.PropertySourcesPropertyResolver;

public class PropertyResolverUtils {
    
    static final Logger logger = LoggerFactory.getLogger(PropertyResolverUtils.class);

    // LONG
    public static Optional<Long> getLong(PropertyResolver props, String key) {
        return Optional.ofNullable(getLong(props, key, (Long)null));
    }
    public static Long getLong(PropertyResolver props, String key, Long defaultValue) {
        return getObject(props, key, defaultValue, Long.class);
    }

    // DOUBLE
    public static Optional<Double> getDouble(PropertyResolver props, String key) {
        return Optional.ofNullable(getDouble(props, key, (Double)null));
    }
    public static Double getDouble(PropertyResolver props, String key, Double defaultValue) {
        return getObject(props, key, defaultValue, Double.class);
    }

    // STRING
    public static Optional<String> getString(PropertyResolver props, String key) {
        return Optional.ofNullable(getString(props, key, (String)null));
    }
    public static String getString(PropertyResolver props, String key, String defaultValue) {
        return getObject(props, key, defaultValue, String.class);
    }

    // STRING[]
    public static Optional<String[]> getStringArray(PropertyResolver props, String key) {
        return Optional.ofNullable(getStringArray(props, key, new String[0]));
    }
    public static String[] getStringArray(PropertyResolver props, String key, String[] defaultValue) {
        return getObject(props, key, defaultValue, String[].class);
    }

    // INTEGER
    public static Optional<Integer> getInteger(PropertyResolver props, String key) {
        return Optional.ofNullable(getInteger(props, key, (Integer)null));
    }
    public static Integer getInteger(PropertyResolver props, String key, Integer defaultValue) {
        return getObject(props, key, defaultValue, Integer.class);
    }

    // BOOLEAN
    public static Optional<Boolean> getBoolean(PropertyResolver props, String key) {
        return Optional.ofNullable(getBoolean(props, key, (Boolean)null));
    }
    public static Boolean getBoolean(PropertyResolver props, String key, Boolean defaultValue) {
        return getObject(props, key, defaultValue, Boolean.class);
    }
    
    private static <T> T getObject(PropertyResolver props, String key, T defaultValue, Class<T> clazz) {
        assert !isNull(clazz);
        if (props == null || key == null) return null;

        return castObject(props.getProperty(key), clazz).orElseGet(useDefault(key, defaultValue));
    }
    private static <T> Supplier<? extends T> useDefault(String key, T defaultValue) {
        return () -> {
            logger.debug("Using default value {} for key {}", defaultValue, key);
            return defaultValue;
        };
    }
    public static PropertyResolver convertProperties(String name, Properties properties) {
        MutablePropertySources s = new MutablePropertySources();
        s.addFirst(new PropertiesPropertySource(name, properties));
        return new PropertySourcesPropertyResolver(s);
    }

}
