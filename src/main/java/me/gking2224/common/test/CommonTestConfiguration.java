package me.gking2224.common.test;

import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.TestPropertySource;

import me.gking2224.common.CommonConfiguration;

@Import(CommonConfiguration.class)
@ImportResource("classpath:test-config.xml")
@TestPropertySource("/test.properties")
public class CommonTestConfiguration {

}
