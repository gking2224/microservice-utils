package me.gking2224.common.client;

import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.util.StringUtils;

public class PropertiesPropertySource extends EnumerablePropertySource<Properties>{

    public PropertiesPropertySource(final String name, final Properties source) {
        super(name, source);
    }
    @Override
    public String[] getPropertyNames() {
        return StringUtils.toStringArray(toStrings(this.source.keySet()));
    }

    private Set<String> toStrings(Set<Object> keySet) {
        return keySet.stream().map(k -> (String)k).collect(Collectors.toSet());
    }
    
    @Override
    public Object getProperty(String name) {
        return source.getProperty(name);
    }

}
