package me.gking2224.common.web;

import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

public class WebAppConfigurer extends WebMvcConfigurerAdapter {

    
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
//            .setPathMatcher(antPathMatcher())
//            .setUrlPathHelper(urlPathHelper())
            ;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
    }
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        CorsRegistration mapping = registry.addMapping("/**");
//        mapping.allowedMethods(options.getAllowedCorsMethods());
//        mapping.allowedOrigins(options.getAllowedCorsOrigins());
    }
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer){
      configurer.enable();
    }
}
