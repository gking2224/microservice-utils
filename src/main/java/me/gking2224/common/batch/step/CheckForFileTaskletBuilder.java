package me.gking2224.common.batch.step;

import static java.lang.String.format;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.env.PropertyResolver;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

import me.gking2224.common.batch.BackOffPolicyBuilder;
import me.gking2224.common.batch.RetryPolicyBuilder;
import me.gking2224.common.batch.generic.AbstractTaskletBuilder;
import me.gking2224.common.utils.ObjectOrSupplier;

public class CheckForFileTaskletBuilder extends AbstractTaskletBuilder<CheckForFileTaskletBuilder> {
    
    private static Logger logger = LoggerFactory.getLogger(CheckForFileTaskletBuilder.class);
    
    private ObjectOrSupplier<File> file;
    private BackOffPolicy backOffPolicy = null;
    private RetryPolicy retryPolicy = null;

    private PropertyResolver properties;
    
    public CheckForFileTaskletBuilder() {
    }
    
    public CheckForFileTaskletBuilder file(final File file) {
        this.file = new ObjectOrSupplier<File>(file);
        return this;
    }
    
    public CheckForFileTaskletBuilder file(final Supplier<File> file) {
        this.file = new ObjectOrSupplier<File>(file);
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
    
    public CheckForFileTaskletBuilder properties(final PropertyResolver properties) {
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
                
                File f = file.get();

                RetryTemplate retryTemplate = new RetryTemplate();
                retryTemplate.setRetryPolicy(retryPolicy);
                retryTemplate.setBackOffPolicy(backOffPolicy);
                
                logger.debug("Starting to look for file {} with retryPolicy [{}] and backOffPolicy [{}]", f, retryPolicy, backOffPolicy);
                Boolean success = retryTemplate.execute(new RetryCallback<Boolean, FileNotFoundException>() {

                    @Override
                    public Boolean doWithRetry(RetryContext context) throws FileNotFoundException {
                        logger.debug("Checking for existence of file {}", f);
                        if (!f.exists()) throw new FileNotFoundException(format("File does not exist: %s", f.getAbsolutePath()));
                        return true;
                    }
                });
                if (!success) {
                    throw new FileNotFoundException(f.getAbsolutePath());
                }
                else return RepeatStatus.FINISHED;
            }

            private BackOffPolicy backOffPolicy(PropertyResolver properties) {
                return new BackOffPolicyBuilder().properties(properties).build();
            }

            private RetryPolicy retryPolicy(PropertyResolver properties) {
                return new RetryPolicyBuilder().properties(properties).build();
            }
        };
    }
}