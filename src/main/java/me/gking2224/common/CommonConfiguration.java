package me.gking2224.common;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import me.gking2224.common.aop.CommonAopConfiguration;
import me.gking2224.common.batch.CommonBatchConfiguration;
import me.gking2224.common.client.CommonClientConfiguration;
import me.gking2224.common.client.EnvironmentProperties;
import me.gking2224.common.client.MicroServiceEnvironment;
import me.gking2224.common.client.jms.CommonMessagingConfiguration;
import me.gking2224.common.db.CommonDatabaseConfiguration;
import me.gking2224.common.db.embedded.CommonEmbeddedDatabaseConfiguration;
import me.gking2224.common.jmx.CommonJmxConfiguration;
import me.gking2224.common.utils.JsonUtil;
import me.gking2224.common.web.CommonWebAppConfiguration;

@Import({
    CommonDatabaseConfiguration.class, CommonEmbeddedDatabaseConfiguration.class,
    CommonBatchConfiguration.class,
    CommonJmxConfiguration.class,
    CommonWebAppConfiguration.class,
    CommonClientConfiguration.class,
    CommonMessagingConfiguration.class,
    CommonAopConfiguration.class
})
@ComponentScan({"me.gking2224.common.utils", "me.gking2224.common.client"})
@EnvironmentProperties(value="props:/environment.properties", prefix="env", name="common-env")
public class CommonConfiguration {

    private static final String DATE_SEPARATOR = "-";
    private static final String DATE_TIME_SEPARATOR = " ";
    private static final String TIME_SEPARATOR = ":";
    private static final String MILLIS_SEPARATOR = ".";
    
    @Autowired MicroServiceEnvironment env;

    @Bean(name="longDateTimeFormat") DateTimeFormatter getLongDateTimeFormatter() {
        return DateTimeFormatter.RFC_1123_DATE_TIME;
    }

    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        Jackson2ObjectMapperBuilder b = new Jackson2ObjectMapperBuilder();
        b.indentOutput(true).dateFormat(new SimpleDateFormat(env.getRequiredProperty("env.simple.date.format")));
        return b;
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
        return DateTimeFormatter.ofPattern(env.getRequiredProperty("env.filename.date.format"));
    }
    
    @Bean public JsonUtil jsonUtil() {
        return new JsonUtil();
    }
}
