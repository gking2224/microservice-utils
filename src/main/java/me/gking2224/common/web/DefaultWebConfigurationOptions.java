package me.gking2224.common.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import me.gking2224.common.client.MicroServiceEnvironment;

@Component
public class DefaultWebConfigurationOptions implements WebConfigurationOptions {
    
    private static final String[] DEFAULT_CORS_ALLOWED_ORIGINS = new String[0];
    private static final String[] DEFAULT_CORS_ALLOWED_METHODS = new String[] {"GET", "OPTIONS"};
    
    @Autowired MicroServiceEnvironment env;

    @Override
    public String[] getAllowedCorsOrigins() {
        return env.getProperty("security.cors.allowedOrigins", String[].class, DEFAULT_CORS_ALLOWED_ORIGINS);
    }

    @Override
    public String[] getAllowedCorsMethods() {
        return env.getProperty("security.cors.allowedMethods", String[].class, DEFAULT_CORS_ALLOWED_METHODS);
    }
    
}
