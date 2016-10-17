package me.gking2224.common.jms;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@Profile("embedded")
public class EmbeddedMessagingConnectionFactoryConfiguration {
    
    @Bean("mqConnectionFactory") ConnectionFactory targetConnectionFactory() {
        ActiveMQConnectionFactory rv = new ActiveMQConnectionFactory();
        rv.setBrokerURL("vm://localhost?broker.persistent=false");
        
        return rv;
    }
}
