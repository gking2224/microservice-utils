package me.gking2224.common.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("/security.properties")
public class DefaultWebConfigurationOptions implements WebConfigurationOptions {
    
    @Value("${allowedCorsOrigins}")
    private String[] allowedCorsOrigins;
    
    @Value("${allowedCorsMethods}")
    private String[] allowedCorsMethods;

    @Override
    public String[] getAllowedCorsOrigins() {
        return allowedCorsOrigins;
    }

    @Override
    public String[] getAllowedCorsMethods() {
        return allowedCorsMethods;
    }
    
}
