package me.gking2224.common.batch;

import static me.gking2224.common.jmx.CommonJmxConfiguration.JMX_MBEAN_GROUP_NAME_PREFIX;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.exception.ExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.batch.BatchDatabaseInitializer;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.retry.backoff.BackOffPolicy;

import me.gking2224.common.client.EnvironmentProperties;
import me.gking2224.common.utils.PropertyResolverUtils;

@EnvironmentProperties(value="props:/batch.properties", name="common-batch", prefix="batch")
@Profile("batch")
@ComponentScan("me.gking2224.common.batch")
@EnableBatchProcessing
@ManagedResource(objectName=CommonBatchConfiguration.SERVICE_NAME_BATCH, description="Batch Service",log=true, logFile="jmx.log", persistPolicy="Never")
public class CommonBatchConfiguration {
    
    public static final String SERVICE_NAME_BATCH = JMX_MBEAN_GROUP_NAME_PREFIX + "Common Batch Configuration";

    private static Logger logger = LoggerFactory.getLogger(CommonBatchConfiguration.class);
    
    @Autowired JobRegistry jobRegistry;

    @Autowired JobRunner jobRunner;

    @Autowired JobExplorer jobExplorer;
    
    @Bean
    public BatchDatabaseInitializer batchDatabaseInitializer() {
        return new BatchDatabaseInitializer();
    }
    @Bean
    public BatchProperties autoConfigureBatchProperties() {
        BatchProperties rv = new BatchProperties();
        return rv;
    }
    
    @Autowired JobParametersBuilderBuilder paramBuilder;
    
    @Autowired JobLauncher jobLauncher;

    @Bean(name="batchProperties")
    @ConditionalOnMissingBean(name="batchProperties")
    public Properties getBatchProperties(@Qualifier("common-batch") Properties commonBatchProperties) throws IOException {
        return commonBatchProperties;
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
        
        return new BackOffPolicyBuilder().properties(
                PropertyResolverUtils.convertProperties("batchProperties", batchProperties)).build();
    }

    @ManagedOperation(description="Run batch job")
    @ManagedOperationParameters({
        @ManagedOperationParameter(name="jobName", description="Job name")
    })
    public String runBatchJob(final String jobName) {
        
        JobRunResult result = jobRunner.runJob(jobName);
        return result.getStatusMessage();
    }

    @ManagedOperation(description="Get batch status")
    @ManagedOperationParameters({
        @ManagedOperationParameter(name="jobExecutionId", description="Job execution id")
    })
    public String getBatchStatus(final Long jobExecutionId) {

        JobExecution j = jobExplorer.getJobExecution(jobExecutionId);

        return new JobExecutionBean(j).toString();
    }
}
