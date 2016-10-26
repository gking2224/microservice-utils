package me.gking2224.common.client.jms;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import me.gking2224.common.client.MicroServiceEnvironment;

@Profile("!embeddedjms")
public class MessagingConnectionFactoryConfiguration {

    private static final Integer DEFAULT_ACTIVEMQ_PORT = 61616;

    private static final String DEFAULT_ACTIVEMQ_HOST = "localhost";
    
    @Bean("mqConnectionFactory") ConnectionFactory targetConnectionFactory(final MicroServiceEnvironment env) {
        int port = env.getProperty("jms.activemq.port", Integer.class, DEFAULT_ACTIVEMQ_PORT);
        String host = env.getProperty("jms.activemq.host", String.class, DEFAULT_ACTIVEMQ_HOST);
        ActiveMQConnectionFactory rv = new ActiveMQConnectionFactory();
        rv.setBrokerURL(String.format("tcp://%s:%d", host, port));
        return rv;
    }
}
