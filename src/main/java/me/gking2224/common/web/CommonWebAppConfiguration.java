package me.gking2224.common.web;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.format.FormatterRegistry;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.util.UrlPathHelper;

@ImportResource("classpath:webapp-config.xml")
@ComponentScan("me.gking2224.common.web")
public class CommonWebAppConfiguration extends WebMvcConfigurerAdapter implements InitializingBean, ApplicationContextAware {

    @Autowired
    private WebConfigurationOptions options;
    
    @SuppressWarnings("unused")
    private ApplicationContext applicationContext;
    
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
       PropertySourcesPlaceholderConfigurer p = new PropertySourcesPlaceholderConfigurer();
       p.setLocations(
               new ClassPathResource("webapp.properties"),
               new ClassPathResource("datasource.properties"));
       return p;
    }
    
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
        registry.jsp();
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LocaleInterceptor());
    }
    
    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
        registry.addResourceHandler("**/*.jsp").addResourceLocations("WEB-INF/jsp", "/");
    }
    
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer
            .setUseSuffixPatternMatch(false)
            .setUseTrailingSlashMatch(true)
            .setUseRegisteredSuffixPatternMatch(true)
            .setPathMatcher(antPathMatcher())
            .setUrlPathHelper(urlPathHelper());
    }
    
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
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
    public void addFormatters(FormatterRegistry registry) {
//        registry.addConverter(new StringToVersion());
//        registry.addConverter(new VersionToString());
//        registry.addConverter(new StringToModelExecutionRequest());
//        modelObjectConverters.getConverters().stream()
//            .forEach(b->registry.addConverter((Converter<?,?>)b));
    }
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        CorsRegistration mapping = registry.addMapping("/**");
        mapping
            .allowedMethods("PUT", "DELETE", "POST", "GET");
        mapping.allowedOrigins(options.getAllowedCorsOrigins());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    
}
