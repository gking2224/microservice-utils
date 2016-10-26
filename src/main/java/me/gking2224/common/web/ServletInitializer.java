package me.gking2224.common.web;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.DispatcherServlet;

@Component
public class ServletInitializer implements ServletContextInitializer, ApplicationContextAware {
    
    private static final EnumSet<DispatcherType> DISPATCHER_TYPES = EnumSet.of(
            DispatcherType.FORWARD);
    
    private ApplicationContext applicationContext;
    
    @Autowired ErrorHandlingFilter errorHandlingFilter;
    
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        registerFilters(servletContext);
        registerServlets(servletContext);
        
    }
    protected void registerServlets(ServletContext servletContext) {
        servletContext.addServlet("dispatcher", dispatcherServlet())
            .addMapping("/*");
    }
    protected void registerFilters(ServletContext servletContext) {
        servletContext.addFilter("errorHandler", errorHandlingFilter).addMappingForUrlPatterns(
                DISPATCHER_TYPES, false, "/*");
        
    }
    @Bean
    public Servlet dispatcherServlet() {
        DispatcherServlet ds = new DispatcherServlet();   
        ds.setApplicationContext(applicationContext);
        return ds;
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
