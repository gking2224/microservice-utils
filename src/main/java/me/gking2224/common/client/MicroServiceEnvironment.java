package me.gking2224.common.client;

import java.util.Set;

import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

public interface MicroServiceEnvironment extends ConfigurableEnvironment {

    String getAppPrefix();
    
    void registerEnvironmentPropertiesAsBeans(GenericApplicationContext applicationContext);

    void addEnvironmentProperties(PropertiesPropertySource propertySource);

    String getEnv();

    Set<String> getApps();
}
