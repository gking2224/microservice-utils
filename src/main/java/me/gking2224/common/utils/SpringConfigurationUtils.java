package me.gking2224.common.utils;

import java.util.Properties;
import java.util.function.Supplier;

import org.springframework.beans.factory.BeanInitializationException;

public class SpringConfigurationUtils {

    public static String getInitProperty(final Properties properties, final String property) throws BeanInitializationException {
        return PropertyUtils.getString(properties, property).orElseThrow(initPropMissing(property));
    }

    public static String getInitProperty(final Properties properties, final String property, final String defaultValue) {
        return PropertyUtils.getString(properties, property).orElse(defaultValue);
    }
    
    public static Supplier<? extends BeanInitializationException> initPropMissing(final String property) {
        return () -> new BeanInitializationException(String.format("Missing property: %s", property));
    }
}
