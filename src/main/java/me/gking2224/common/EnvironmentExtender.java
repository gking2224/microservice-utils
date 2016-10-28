package me.gking2224.common;

import static java.lang.String.format;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.reflections.Reflections;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ProtocolResolver;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.StringUtils;

import me.gking2224.common.client.EnvironmentProperties;
import me.gking2224.common.client.MicroServiceEnvironment;
import me.gking2224.common.utils.NestedProperties;

public class EnvironmentExtender {

    private static final String PROPS_DIR_PROPERTY = "PROPS_DIR";
    
    private DefaultResourceLoader resourceLoader =  new DefaultResourceLoader();
    private MicroServiceEnvironment env;
    
    public EnvironmentExtender(MicroServiceEnvironment env) {
        this.env = env;
        resourceLoader.addProtocolResolver(new PropsDirProtocolResolver());
    }
    
    public void extendEnvironmentWithAnnotatedProperties(
            final String basePackage) {
        
        Reflections r = new Reflections(basePackage);
        Set<Class<?>> cc = r.getTypesAnnotatedWith(EnvironmentProperties.class);
        Set<String> profiles = new HashSet<String>(Arrays.asList(env.getActiveProfiles()));
        cc.forEach(c -> {
            Profile profile = c.getAnnotation(Profile.class);
            if (profile == null || profileMatches(profile, profiles)) {
                EnvironmentProperties ec = c.getAnnotation(EnvironmentProperties.class);
                addPropertySourceToEnvironment(ec.prefix(), ec.name(), ec.value());
            }
        });
    }

    private boolean profileMatches(final Profile p, final Set<String> profiles) {
        boolean rv = false;
        for (String profile : p.value()) {
            if (profile.startsWith("!")) {
                if (profiles.contains(profile.substring(1))) return false;
                else rv = true;
            }
            if (profiles.contains(profile)) rv = true;
        }
        return rv;
    }

    private void addPropertySourceToEnvironment(
            final String prefix, final String name, final String location) {
        String locationToUse = this.env.resolveRequiredPlaceholders(location);
        try {
//            String env = this.env);
            String nameToUse = name;
            if (!StringUtils.hasLength(nameToUse)) nameToUse = getNameFromLocation(locationToUse);
            String prefixToUse = prefix;
            if (!StringUtils.hasLength(prefixToUse)) prefixToUse = nameToUse;
            
            Properties fallback = getNestedProperties(locationToUse, null, null);
            Properties p = getNestedProperties(locationToUse, this.env.getAppPrefix(), fallback);
            if (p == null) p = fallback;
            me.gking2224.common.client.PropertiesPropertySource propertySource = new PrefixedPropertiesPropertySource(
                    prefixToUse, nameToUse, p);
            this.env.addEnvironmentProperties(propertySource);
            if (ConfigurableEnvironment.class.isAssignableFrom(this.env.getClass())) {
                ((ConfigurableEnvironment)this.env).getPropertySources().addLast(propertySource);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getNameFromLocation(final String propertiesPath) {
        String rv = propertiesPath;
        int lst = rv.lastIndexOf("/");
        if (lst != -1) {
            rv = rv.substring(lst+1);
        }
        int sfx = rv.indexOf(".properties");
        if (sfx != -1) {
            rv = rv.substring(0, sfx);
        }
        return rv;
    }

    private Properties getNestedProperties(
            final String location, final String appPrefix, final Properties fallback
    )
    throws IOException {
        Properties p = getProperties(getLocation(location, appPrefix), fallback);
        
        return (p == null) ? null : new NestedProperties(this.env.getEnv(), p);
    }

    private String getLocation(String location, String appPrefix) {
        if (appPrefix != null) {
            String rv = location;
            int lst = rv.lastIndexOf("/");
            return new StringBuilder()
                    .append(rv.substring(0, lst+1))
                    .append(appPrefix)
                    .append("-")
                    .append(rv.substring(lst+1))
                    .toString();
        }
        else return location;
    }

    private Properties getProperties(String location, Properties fallback) throws IOException {
        try {
            Properties rawProps = PropertiesLoaderUtils.loadProperties(resourceLoader.getResource(location));
            if (fallback == null) {
                return rawProps;
            }
            else {
                Properties p = new Properties(fallback);
                p.putAll(rawProps);
                return p;
            }
        }
        catch (FileNotFoundException e) {
            return null;
        }
    }
    
    private class PropsDirProtocolResolver implements ProtocolResolver {

        final String FILE_PREFIX = "file://";
        final String PROPS_PREFIX = "props:";
        final String SLASH = "/";
        private String propsDir;
        @Override
        public Resource resolve(String location, ResourceLoader resourceLoader) {
            if (location.startsWith(PROPS_PREFIX)) {
                String propsDir;
                try {
                    propsDir = getPropsDir();
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                String remainder = location.substring(PROPS_PREFIX.length());
                if (!remainder.startsWith(SLASH)) remainder = SLASH + remainder;
                String locationToUse = propsDir + remainder;
                return resourceLoader.getResource(locationToUse);
            }
            return null;
        }
        
        private String getPropsDir() throws FileNotFoundException {
            if (propsDir == null) {
                propsDir = env.getProperty(PROPS_DIR_PROPERTY);
                if (propsDir == null) {
                    propsDir = env.getRequiredProperty("user.home")+"/properties";
                }
                File dir = new File(propsDir);
                if (!dir.exists()) {
                    throw new FileNotFoundException(format("Properties directory %s does not exist", propsDir));
                }
                else if (!dir.isDirectory()) {
                    throw new FileNotFoundException(format("Properties directory %s is not a directory", propsDir));
                }
                propsDir = dir.getAbsolutePath();
            }
            return FILE_PREFIX + propsDir;
        }
    }
}
