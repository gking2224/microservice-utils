package me.gking2224.common.client;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CommonClientConfiguration {

    @Bean
    public RestTemplate restTemplate(ErrorResponseThrowingResponseErrorHandler errorHandler) {
        CacheConfig cacheConfig = CacheConfig.custom().setMaxCacheEntries(1000).setMaxObjectSize(2048).build();
        CloseableHttpClient client = CachingHttpClientBuilder.create().setCacheConfig(cacheConfig).build();
        ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(client);
        RestTemplate rv = new RestTemplate(requestFactory);
        rv.setErrorHandler(errorHandler);
        return rv;
    }
}
