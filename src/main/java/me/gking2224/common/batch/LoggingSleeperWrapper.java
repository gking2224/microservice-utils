package me.gking2224.common.batch;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.backoff.Sleeper;

public class LoggingSleeperWrapper implements Sleeper {
    
    DateTimeFormatter df = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);

    private static Logger logger = LoggerFactory.getLogger(LoggingSleeperWrapper.class);
    private Sleeper delegate;

    public LoggingSleeperWrapper(Sleeper delegate) {
        this.delegate = delegate;
    }
    @Override
    public void sleep(long backOffPeriod) throws InterruptedException {
        Duration duration = Duration.ofMillis(backOffPeriod);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime until = now.plus(duration);
        logger.debug("Sleeping for {} millis ({})", backOffPeriod, df.format(until));
        delegate.sleep(backOffPeriod);
    }

}
