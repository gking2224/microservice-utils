package me.gking2224.common.web;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClientBuilder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
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
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.DispatcherServlet;
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

import me.gking2224.common.client.ErrorResponse;
import me.gking2224.common.client.ErrorResponseException;
import me.gking2224.common.utils.ObjectSerializationUtil;
import me.gking2224.common.utils.ObjectSerializer.ObjectSerializationException;

@ImportResource("classpath:webapp-config.xml")
@ComponentScan("me.gking2224.common.web")
public class CommonWebAppConfiguration extends WebMvcConfigurerAdapter implements ApplicationContextAware {

    @Autowired
    private WebConfigurationOptions options;
    
    private ApplicationContext applicationContext;
    
    @Autowired
    private ObjectSerializationUtil serializationUtil;
    
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
    }
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        CorsRegistration mapping = registry.addMapping("/**");
        mapping
            .allowedMethods("PUT", "DELETE", "POST", "GET");
        mapping.allowedOrigins(options.getAllowedCorsOrigins());
    }

    @Bean
    public RestTemplate restTemplate() {
        CacheConfig cacheConfig = CacheConfig.custom().setMaxCacheEntries(1000).setMaxObjectSize(2048).build();
        CloseableHttpClient client = CachingHttpClientBuilder.create().setCacheConfig(cacheConfig).build();
        ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(client);
        RestTemplate rv = new RestTemplate(requestFactory);
        rv.setErrorHandler(errorHandler());
        return rv;
    }

    @Bean
    public ResponseErrorHandler errorHandler() {
        
        ResponseErrorHandler rv = new ResponseErrorHandler() {
            
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return response.getRawStatusCode() != 200;
            }
            
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                
                final MediaType mediaType = response.getHeaders().getContentType();
                String responseBody = IOUtils.toString(response.getBody(), Charset.defaultCharset());
                try {
                    throw new ErrorResponseException(
                            serializationUtil.deserializeToObject(responseBody, mediaType, ErrorResponse.class));
                }
                catch (ObjectSerializationException e) {
                    throw new ErrorResponseException(
                            new ErrorResponse(response.getStatusCode().value(), response.getStatusText()));
                }
            }
        };
        return rv;
    }
    
    @Bean
    public FilterRegistrationBean errorHandlingFilterBean(ErrorHandlingFilter filter) {
        final FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(filter);
        filterRegistrationBean.setOrder(0);
        filterRegistrationBean.setEnabled(true);
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer){
      configurer.enable();
    }

    @Bean
    public ServletRegistrationBean foo() {
        DispatcherServlet dispatcherServlet = new DispatcherServlet();   
        dispatcherServlet.setApplicationContext(applicationContext);
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(dispatcherServlet, "/*");
        servletRegistrationBean.setName("dispatcher");
        return servletRegistrationBean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
