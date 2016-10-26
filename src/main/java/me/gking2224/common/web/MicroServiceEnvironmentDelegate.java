package me.gking2224.common.web;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.GenericApplicationContext;

import me.gking2224.common.cli.CommandLineHelper.CommandLine;
import me.gking2224.common.client.PropertiesPropertySource;

public class MicroServiceEnvironmentDelegate {

    private String env;
    private Set<String> apps;
    private String appPrefix;
    private File propsDir;
    
    private List<PropertiesPropertySource> environmentProperties = new ArrayList<PropertiesPropertySource>();

    public MicroServiceEnvironmentDelegate(CommandLine cl, String appPrefix) {
        this.env = cl.getEnv();
        this.apps = cl.getApps();
        this.propsDir = cl.getPropsDir();
        this.appPrefix = appPrefix;
    }
    
    String[] getActiveProfiles(CommandLine cl) {
        Set<String> profiles = new HashSet<String>();
        cl.getApps().forEach(profiles::add);
        profiles.add(cl.getEnv());
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

    public File getPropsDir() {
        return propsDir;
    }
}
