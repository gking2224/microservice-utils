package me.gking2224.common.web;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.util.UrlPathHelper;

import me.gking2224.common.client.EnvironmentProperties;

@ImportResource("classpath:webapp-config.xml")
@ComponentScan("me.gking2224.common.web")
@Profile("web")
@EnvironmentProperties(value="props:/webapp.properties", name="common-webapp", prefix="web")
public class CommonWebAppConfiguration implements ApplicationContextAware {

    @SuppressWarnings("unused")
    @Autowired
    private WebConfigurationOptions options;
    
    @SuppressWarnings("unused")
    private ApplicationContext applicationContext;
    
    @Bean
    public ApplicationListener<ContextRefreshedEvent> contextRefreshed() {
        return new ContextInitFinishListener();
    }
    
    @Bean
    public FreeMarkerConfigurer getFreeMarkerConfig() {
        FreeMarkerConfigurer fmc = new FreeMarkerConfigurer();
        fmc.setTemplateLoaderPath("/WEB-INF/ftl");
        return fmc; 
    }
    
    @Bean
    public FreeMarkerConfigurationFactoryBean getFreeMarkerConfigFactory() {
        FreeMarkerConfigurationFactoryBean f = new FreeMarkerConfigurationFactoryBean();
        f.setTemplateLoaderPath("/WEB-INF/ftl");
        return f;
    }

    @Bean
    public UrlPathHelper urlPathHelper() {
        UrlPathHelper helper = new UrlPathHelper();
        return helper;
    }
    
    @Bean
    public PathMatcher antPathMatcher() {
        AntPathMatcher matcher = new AntPathMatcher();
        return matcher;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
