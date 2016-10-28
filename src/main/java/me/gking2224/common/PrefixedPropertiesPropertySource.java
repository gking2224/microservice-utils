package me.gking2224.common;

import java.util.Properties;

import me.gking2224.common.client.PropertiesPropertySource;
import me.gking2224.common.utils.PrefixedProperties;

public class PrefixedPropertiesPropertySource extends PropertiesPropertySource {

    private String prefix;

    public PrefixedPropertiesPropertySource(String prefix, String name, Properties source) {
//        super(name, source);
//        this.prefix = prefix;
        super(name, new PrefixedProperties(prefix, source));
    }
    
    @Override
    public Object getProperty(String name) {
        if (!name.startsWith(prefix)) return null;
        return super.getProperty(name.substring(prefix.length()+1));
    }

    @Override
    public boolean containsProperty(String name) {
        if (!name.startsWith(prefix)) return false;
        return super.containsProperty(name.substring(prefix.length()+1));
    }

    @Override
    public String[] getPropertyNames() {
        return super.getPropertyNames();
    }

    @Override
    public String toString() {
        return String.format("PrefixedPropertiesPropertySource [prefix=%s, parent=%s]", prefix, super.toString());
    }
}
