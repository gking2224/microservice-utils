package me.gking2224.common.batch;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.exception.ExceptionHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.retry.backoff.BackOffPolicy;

import me.gking2224.common.utils.NestedProperties;

@Configuration
@PropertySource("/batch.properties")
@Profile("batch")
public class CommonBatchConfiguration {
    
    private static Logger logger = LoggerFactory.getLogger(CommonBatchConfiguration.class);

    @Bean(name="batchProperties")
    public Properties getBatchProperties() throws IOException {
        Properties p = PropertiesLoaderUtils.loadProperties(new ClassPathResource("/batch.properties"));
        
        return new NestedProperties("batch", p);
    }
    
    @Bean("defaultJobParameters")
    public JobParametersBuilderBuilder defaultJobParameters() {
        return new JobParametersBuilderBuilder().addBatchDate().addExecutionDate();
    }

    @Bean("loggingExceptionHandler")
    public ExceptionHandler loggingExceptionHandler() {
        return new ExceptionHandler() {
            @Override
            public void handleException(RepeatContext context, Throwable t) throws Throwable {
                logger.error("Handle error", t);
            }
        };
    }
    
    @Bean("defaultBackOffPolicy")
    public BackOffPolicy defaultBackOffPolicy(@Qualifier("batchProperties") final Properties batchProperties) {
        
        return new BackOffPolicyBuilder().properties(batchProperties).build();
    }
}
