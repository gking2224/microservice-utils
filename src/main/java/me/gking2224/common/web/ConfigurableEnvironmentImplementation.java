package me.gking2224.common.web;

import java.util.Set;

import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.StandardEnvironment;

import me.gking2224.common.client.MicroServiceEnvironment;
import me.gking2224.common.client.PropertiesPropertySource;

public class ConfigurableEnvironmentImplementation extends StandardEnvironment implements MicroServiceEnvironment {

    private MicroServiceEnvironmentDelegate delegate;

    
    public ConfigurableEnvironmentImplementation(final String appPrefix, final String env, final Set<String> apps) {
        this.delegate = new MicroServiceEnvironmentDelegate(appPrefix, env, apps);
        super.setActiveProfiles(delegate.getActiveProfiles());
    }

    @Override
    public String getEnv() {
        return delegate.getEnv();
    }

    @Override
    public Set<String> getApps() {
        return delegate.getApps();
    }

    @Override
    public void registerEnvironmentPropertiesAsBeans(GenericApplicationContext applicationContext) {
        delegate.registerEnvironmentPropertiesAsBeans(applicationContext);
    }

    @Override
    public void addEnvironmentProperties(PropertiesPropertySource propertySource) {
        delegate.addEnvironmentProperties(propertySource);
    }

    @Override
    public String getAppPrefix() {
        return delegate.getAppPrefix();
    }
}