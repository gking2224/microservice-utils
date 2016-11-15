package me.gking2224.common.client;

public interface WebConfigurationOptions {

    
    String[] getAllowedCorsOrigins();
    
    String[] getAllowedCorsMethods();

    String[] getAllowedRequestHeaders();
}
