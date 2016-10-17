package me.gking2224.common.jms;

import java.io.IOException;
import java.util.Properties;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

@Profile("!embedded")
@PropertySource("/activemq.properties")
public class MessagingConnectionFactoryConfiguration {

    @Value("${activemq.port}")
    int activeMqPort;

    @Value("${activemq.host}")
    String activeMqHost;
    
    @Bean(name="activeMqProperties")
    public Properties properties() throws IOException {
        Properties dsProps = PropertiesLoaderUtils.loadProperties(new ClassPathResource("/activemq.properties"));
        return dsProps;
    }
    
    @Bean("mqConnectionFactory") ConnectionFactory targetConnectionFactory(@Qualifier("activeMqProperties") Properties mqProperties/*, BrokerService broker*/) {
        ActiveMQConnectionFactory rv = new ActiveMQConnectionFactory();
        rv.setBrokerURL(String.format("tcp://%s:%d", activeMqHost, activeMqPort));
        return rv;
    }
}
