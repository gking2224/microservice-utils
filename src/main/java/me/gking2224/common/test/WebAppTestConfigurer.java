package me.gking2224.common.test;


import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.format.FormatterRegistry;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import me.gking2224.common.web.ContextInitFinishListener;
import me.gking2224.common.web.LocaleInterceptor;

@ImportResource("classpath:test-webapp-config.xml")
@Configuration
@EnableWebMvc
public class WebAppTestConfigurer extends WebMvcConfigurerAdapter {

//    @Autowired
//    ModelObjectConverters modelObjectConverters;
    
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
    
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.freeMarker().prefix("").suffix(".ftl");
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LocaleInterceptor());
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
    }
    
}
