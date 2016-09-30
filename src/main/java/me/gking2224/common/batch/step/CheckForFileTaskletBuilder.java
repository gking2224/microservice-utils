package me.gking2224.common.batch.step;

import static java.lang.String.format;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

import me.gking2224.common.batch.BackOffPolicyBuilder;
import me.gking2224.common.batch.RetryPolicyBuilder;

public class CheckForFileTaskletBuilder {
    
    private static Logger logger = LoggerFactory.getLogger(CheckForFileTaskletBuilder.class);
    
    private File file;
    private BackOffPolicy backOffPolicy = null;
    private RetryPolicy retryPolicy = null;

    private Properties properties;
    
    public CheckForFileTaskletBuilder() {
    }
    
    public CheckForFileTaskletBuilder file(final File file) {
        this.file = file;
        return this;
    }
    
    public CheckForFileTaskletBuilder retryPolicy(final RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
        return this;
    }
    
    public CheckForFileTaskletBuilder backOffPolicy(final BackOffPolicy backOffPolicy) {
        this.backOffPolicy = backOffPolicy;
        return this;
    }
    
    public CheckForFileTaskletBuilder properties(final Properties properties) {
        this.properties = properties;
        return this;
    }
    
    public Tasklet build() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                
                if (backOffPolicy == null) {
                    backOffPolicy = backOffPolicy(properties);
                }
                if (retryPolicy == null) {
                    retryPolicy = retryPolicy(properties);
                }

                RetryTemplate retryTemplate = new RetryTemplate();
                retryTemplate.setRetryPolicy(retryPolicy);
                retryTemplate.setBackOffPolicy(backOffPolicy);
                
                logger.debug("Starting to look for file {} with retryPolicy [{}] and backOffPolicy [{}]", file, retryPolicy, backOffPolicy);
                Boolean success = retryTemplate.execute(new RetryCallback<Boolean, FileNotFoundException>() {

                    @Override
                    public Boolean doWithRetry(RetryContext context) throws FileNotFoundException {
                        logger.debug("Checking for existence of file {}", file);
                        if (!file.exists()) throw new FileNotFoundException(format("File does not exist: %s", file.getAbsolutePath()));
                        return true;
                    }
                });
                if (!success) {
                    throw new FileNotFoundException(file.getAbsolutePath());
                }
                else return RepeatStatus.FINISHED;
            }

            private BackOffPolicy backOffPolicy(Properties properties) {
                return new BackOffPolicyBuilder().properties(properties).build();
            }

            private RetryPolicy retryPolicy(Properties properties) {
                return new RetryPolicyBuilder().properties(properties).build();
            }
        };
    }
}