package me.gking2224.common.batch;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.context.RetryContextSupport;

public class FixedTimeRetryPolicy implements RetryPolicy {

    private static Logger logger = LoggerFactory.getLogger(FixedTimeRetryPolicy.class);
    private ZonedDateTime cutoff;
    
    private FixedTimeRetryPolicy(final ZonedDateTime now, final ZonedDateTime cutoff) {
        super();
        this.cutoff = nextInstantOfTimeInZone(now, cutoff);
    }
    
    public FixedTimeRetryPolicy(final ZonedDateTime cutoff) {
        this(LocalDateTime.now().atZone(cutoff.getZone()), cutoff);
    }
    
    @Override
    public boolean canRetry(RetryContext context) {

        ZonedDateTime now = LocalDateTime.now().atZone(cutoff.getZone());
        boolean retry = now.isBefore(cutoff);
        
        if (retry) {
            logger.debug("Keep retrying until {}", cutoff);
        }
        else {
            logger.debug("Cutoff time {} has passed, do not retry", cutoff);
        }
        return retry;
    }

    @Override
    public RetryContext open(RetryContext parent) {
        return new FixedTimeRetryContext(parent);
    }

    @Override
    public void close(RetryContext context) {

    }

    @Override
    public void registerThrowable(RetryContext context, Throwable throwable) {
    }
    
    @SuppressWarnings("serial")
    private static class FixedTimeRetryContext extends RetryContextSupport {
        public FixedTimeRetryContext(RetryContext parent) {
            super(parent);
        }
    }

    protected final ZonedDateTime nextInstantOfTimeInZone(ZonedDateTime now, ZonedDateTime cutoff) {

        if (cutoff.isBefore(now)) {
            return LocalDateTime.of(
                    LocalDate.from(now).plus(1, ChronoUnit.DAYS),
                    LocalTime.from(cutoff))
                    .atZone(cutoff.getZone());
        }
        else return cutoff;
    }

}
