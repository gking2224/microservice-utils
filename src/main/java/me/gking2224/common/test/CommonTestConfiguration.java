package me.gking2224.common.test;

import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.TestPropertySource;

import me.gking2224.common.CommonConfiguration;

@ImportResource("classpath:test-config.xml")
@TestPropertySource("/test.properties")
@Import(CommonConfiguration.class)
public class CommonTestConfiguration {

}
