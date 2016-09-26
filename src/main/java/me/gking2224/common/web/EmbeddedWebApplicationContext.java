package me.gking2224.common.web;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//@EnableWebMvc
public class EmbeddedWebApplicationContext
        extends org.springframework.boot.context.embedded.EmbeddedWebApplicationContext {

    @Value("${contextPath}")
    private String contextPath;
    
    @Value("${httpPort}")
    private int httpPort;
    
    
    @Override
    @Bean
    public EmbeddedServletContainerFactory getEmbeddedServletContainerFactory() {
        TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
        factory.setPort(httpPort);
        factory.setSessionTimeout(50, TimeUnit.MINUTES);
        factory.setContextPath(contextPath);
        
        return factory;
    }
    
}
