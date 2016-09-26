package me.gking2224.common.web;

import java.time.format.DateTimeFormatter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebappConfiguration {

    @Bean(name="longDateTimeFormat") DateTimeFormatter getLongDateTimeFormatter() {
        return DateTimeFormatter.RFC_1123_DATE_TIME;
    }
}
