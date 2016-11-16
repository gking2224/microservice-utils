package me.gking2224.common.client;

import java.util.Objects;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.client.RestTemplate;

public abstract class AbstractServiceClient implements InitializingBean {
    
    private static final String DEFAULT_CONTEXT = "/";
    private String protocol;
    private String host;
    private int port;
    private String context = DEFAULT_CONTEXT;
    private String baseUrl;
    private RestTemplate restTemplate;
    
    public String getProtocol() {
        return protocol;
    }
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
    
    public String getHost() {
        return host;
    }
    public void setHost(String host) {
        this.host = host;
    }
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }
    public String getContext() {
        return context;
    }
    public void setContext(String context) {
        this.context = context;
    }

    protected String getBaseUrl() {
        Objects.requireNonNull(this.protocol);
        Objects.requireNonNull(this.host);
        Objects.requireNonNull(this.port);
        Objects.requireNonNull(this.context);
        
        if (baseUrl == null) {
            baseUrl = String.format("%s://%s:%d/%s", this.protocol, this.host, this.port, this.context);
        }
        return baseUrl;
    }
    
    public RestTemplate getRestTemplate() {
        return restTemplate;
    }
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

}
