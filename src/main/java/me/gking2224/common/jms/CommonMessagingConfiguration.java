package me.gking2224.common.jms;

import javax.jms.ConnectionFactory;
import javax.jms.Topic;

import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.SingleConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.BeanFactoryDestinationResolver;
import org.springframework.jms.support.destination.DestinationResolver;

@ComponentScan("me.gking2224.common.jms")
@Import({MessagingConnectionFactoryConfiguration.class, EmbeddedMessagingConnectionFactoryConfiguration.class})
@EnableJms
public class CommonMessagingConfiguration implements ApplicationContextAware {

    public static final String QUEUE_LISTENER_CONTAINER_FACTORY = "queueListenerContainerFactory";

    public static final String TOPIC_LISTENER_CONTAINER_FACTORY = "topicListenerContainerFactory";
    
    private ApplicationContext applicationContext;

    /**
     * Standard topic for system events
     * @return
     */
    @Bean("SystemEvents") Topic systemEventsTopic() {
        return new ActiveMQTopic("SystemEvents");
    }

    @Bean
    /**
     * Resolve message destinations from topic/queue beans
     * @return
     */
    public DestinationResolver destinationResolver() {
        BeanFactoryDestinationResolver rv = new BeanFactoryDestinationResolver(applicationContext);
        return rv;
    }

    @Bean(TOPIC_LISTENER_CONTAINER_FACTORY)
    public JmsListenerContainerFactory<?> topicListenerContainerFactory(
            ConnectionFactory connectionFactory,
            DestinationResolver destinationResolver
    ) {
      DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
      factory.setConnectionFactory(connectionFactory);
      factory.setPubSubDomain(true);
      factory.setDestinationResolver(destinationResolver);
      factory.setConcurrency("1");
      return factory;
    }

    @Bean(QUEUE_LISTENER_CONTAINER_FACTORY)
    public JmsListenerContainerFactory<?> queueListenerContainerFactory(
            ConnectionFactory connectionFactory,
            DestinationResolver destinationResolver
    ) {
      DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
      factory.setConnectionFactory(connectionFactory);
      factory.setPubSubDomain(false);
      factory.setDestinationResolver(destinationResolver);
      factory.setConcurrency("1-5");
      return factory;
    }
    
    @Bean
    @Primary
    ConnectionFactory connectionFactory(@Qualifier("mqConnectionFactory") ConnectionFactory targetConnectionFactory) {
        
        SingleConnectionFactory rv = new SingleConnectionFactory();
        rv.setExceptionListener(rv);
        rv.setTargetConnectionFactory(targetConnectionFactory);
        return rv;
    }
    
    @Bean("queueTemplate") JmsTemplate queueTemplate(ConnectionFactory connectionFactory) {
        JmsTemplate rv = new JmsTemplate();
        rv.setConnectionFactory(connectionFactory);
        return rv;
    }
    
    @Bean("pubSubTemplate") JmsTemplate pubSubTemplate(ConnectionFactory connectionFactory) {
        JmsTemplate rv = new JmsTemplate();
        rv.setPubSubDomain(true);
        rv.setConnectionFactory(connectionFactory);
        return rv;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
