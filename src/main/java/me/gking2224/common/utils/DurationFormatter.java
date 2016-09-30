package me.gking2224.common.utils;

import static java.lang.String.format;

import java.time.Duration;
import java.util.function.Function;

public class DurationFormatter implements Function<String, Duration> {

    private static DurationFormatter instance = new DurationFormatter();
    private static final Duration ZERO = Duration.ofMillis(0L);
    
    private DurationFormatter() {
    }
    
    public static final DurationFormatter getInstance() {
        return instance;
    }
    
    @Override
    public Duration apply(String t) {
        
        if (t == null) return ZERO;
        if (t.length() == 0) return ZERO;
        if (t.length() == 1) return Duration.ofMillis(Long.valueOf(t));
        String suffix = t.substring(t.length()-1);
        String value = t.substring(0,  t.length() -1);
        
        switch (suffix) {
        case "h":
            return Duration.ofHours(Long.valueOf(value));
        case "m":
            return Duration.ofMinutes(Long.valueOf(value));
        case "s":
            return Duration.ofSeconds(Long.valueOf(value));
        case "0":
        case "1":
        case "2":
        case "3":
        case "4":
        case "5":
        case "6":
        case "7":
        case "8":
        case "9":
            return Duration.ofMillis(Long.valueOf(t));
        default :
            throw new IllegalArgumentException(format("Unknown suffix %s", suffix));
        }
    }

}
