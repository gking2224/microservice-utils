package me.gking2224.common.web;

import java.io.File;
import java.util.Set;

import org.springframework.context.support.GenericApplicationContext;
import org.springframework.web.context.support.StandardServletEnvironment;

import me.gking2224.common.cli.CommandLineHelper.CommandLine;
import me.gking2224.common.client.MicroServiceEnvironment;

public class ConfigurableWebEnvironmentImplementation
extends StandardServletEnvironment
implements MicroServiceEnvironment {

    private MicroServiceEnvironmentDelegate delegate;

    public ConfigurableWebEnvironmentImplementation(final CommandLine cl, final String appPrefix) {
        this.delegate = new MicroServiceEnvironmentDelegate(cl, appPrefix);
        super.setActiveProfiles(delegate.getActiveProfiles(cl));
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

    @Override
    public File getPropsDir() {
        return delegate.getPropsDir();
    }
}