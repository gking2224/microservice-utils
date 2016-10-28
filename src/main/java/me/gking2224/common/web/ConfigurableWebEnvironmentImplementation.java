package me.gking2224.common.web;

import java.util.Set;

import org.springframework.context.support.GenericApplicationContext;
import org.springframework.web.context.support.StandardServletEnvironment;

import me.gking2224.common.client.MicroServiceEnvironment;

public class ConfigurableWebEnvironmentImplementation
extends StandardServletEnvironment
implements MicroServiceEnvironment {

    private MicroServiceEnvironmentDelegate delegate;

    public ConfigurableWebEnvironmentImplementation(final String appPrefix, final String env, final Set<String> apps) {
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
    public void addEnvironmentProperties(me.gking2224.common.client.PropertiesPropertySource propertySource) {
        delegate.addEnvironmentProperties(propertySource);
    }

    @Override
    public String getAppPrefix() {
        return delegate.getAppPrefix();
    }
}