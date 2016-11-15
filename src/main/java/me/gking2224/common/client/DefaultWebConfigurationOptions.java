package me.gking2224.common.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultWebConfigurationOptions implements WebConfigurationOptions {
    
    private static final String[] DEFAULT_CORS_ALLOWED_ORIGINS = new String[0];
    private static final String[] DEFAULT_CORS_ALLOWED_METHODS = new String[] {"GET", "OPTIONS"};
    private static final String[] DEFAULT_CORS_ALLOWED_HEADERS = new String[] {"*"};
    
    @Autowired MicroServiceEnvironment env;

    @Override
    public String[] getAllowedCorsOrigins() {
        return env.getProperty("security.cors.allowedOrigins", String[].class, DEFAULT_CORS_ALLOWED_ORIGINS);
    }

    @Override
    public String[] getAllowedCorsMethods() {
        return env.getProperty("security.cors.allowedMethods", String[].class, DEFAULT_CORS_ALLOWED_METHODS);
    }

    @Override
    public String[] getAllowedRequestHeaders() {
        return env.getProperty("security.cors.allowedHeaders", String[].class, DEFAULT_CORS_ALLOWED_HEADERS);
    }
}
