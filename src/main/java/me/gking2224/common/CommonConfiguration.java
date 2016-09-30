package me.gking2224.common;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfiguration {

    private static final String DATE_SEPARATOR = "-";
    private static final String DATE_TIME_SEPARATOR = " ";
    private static final String TIME_SEPARATOR = ":";
    private static final String MILLIS_SEPARATOR = ".";

    @Bean(name="longDateTimeFormat") DateTimeFormatter getLongDateTimeFormatter() {
        return DateTimeFormatter.RFC_1123_DATE_TIME;
    }

    @Bean(name="shortDateTimeFormat") DateTimeFormatter getShortDateTimeFormatter() {
        return new DateTimeFormatterBuilder()
                .appendPattern("yyyy")
                .appendLiteral(DATE_SEPARATOR)
                .appendPattern("MM")
                .appendLiteral(DATE_SEPARATOR)
                .appendPattern("dd")
                .appendLiteral(DATE_TIME_SEPARATOR)
                .appendPattern("HH")
                .appendLiteral(TIME_SEPARATOR)
                .appendPattern("MM")
                .appendLiteral(TIME_SEPARATOR)
                .appendPattern("ss")
                .appendLiteral(MILLIS_SEPARATOR)
                .appendPattern("SSS")
                .toFormatter();
    }

    @Bean(name="filenameDateFormat") DateTimeFormatter filenameDateFormatter() {
        return DateTimeFormatter.ofPattern("yyyyMMdd");
    }
}
