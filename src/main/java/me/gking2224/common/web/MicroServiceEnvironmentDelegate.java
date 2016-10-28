package me.gking2224.common.web;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.GenericApplicationContext;

import me.gking2224.common.client.PropertiesPropertySource;

public class MicroServiceEnvironmentDelegate {

    private String env;
    private Set<String> apps;
    private String appPrefix;
    
    private List<PropertiesPropertySource> environmentProperties = new ArrayList<PropertiesPropertySource>();

    public MicroServiceEnvironmentDelegate(String appPrefix, String env, Set<String> apps) {
        this.env = env;
        this.apps = apps;
        this.appPrefix = appPrefix;
    }
    
    String[] getActiveProfiles() {
        Set<String> profiles = new HashSet<String>();
        profiles.addAll(apps);
        profiles.add(env);
        return profiles.toArray(new String[profiles.size()]);
    }

    public String getEnv() {
        return this.env;
    }

    public Set<String> getApps() {
        return this.apps;
    }

    public void registerEnvironmentPropertiesAsBeans(GenericApplicationContext applicationContext) {
        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        this.environmentProperties.forEach(p -> {
            beanFactory.registerSingleton(p.getName(), p.getSource());
        });
    }

    public void addEnvironmentProperties(PropertiesPropertySource propertySource) {
        this.environmentProperties.add(propertySource);
    }

    public String getAppPrefix() {
        return this.appPrefix;
    }
}
