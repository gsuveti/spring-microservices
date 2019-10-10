package com.example.resourceblog.config;

import com.example.resourceblog.events.BrokerEventListener;
import com.example.resourceblog.events.BrokerEventService;
import com.example.resourceblog.events.BrokerEventServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.context.support.GenericWebApplicationContext;

import javax.jms.ConnectionFactory;
import java.util.List;
import java.util.logging.Logger;

@Configuration
@EnableJms
public class JmsConfig implements JmsListenerConfigurer {
    private static final Logger log = Logger.getLogger(JmsConfig.class.toString());

    private final ConnectionFactory connectionFactory;
    private final DefaultJmsListenerContainerFactoryConfigurer configurer;
    private final JmsTemplate jmsTemplate;
    private final BrokerEventService brokerEventService;

    @Autowired(required = false)
    private final List<BrokerEventListener> brokerEvenListeners;

    @Autowired
    private GenericWebApplicationContext context;

    public JmsConfig(ConnectionFactory connectionFactory, DefaultJmsListenerContainerFactoryConfigurer configurer, JmsTemplate jmsTemplate, BrokerEventService brokerEventService, List<BrokerEventListener> brokerEvenListeners) {
        this.connectionFactory = connectionFactory;
        this.configurer = configurer;
        this.jmsTemplate = jmsTemplate;
        this.brokerEventService = brokerEventService;
        this.brokerEvenListeners = brokerEvenListeners;
    }

    @Bean
    public JmsListenerContainerFactory<?> jmsFactory(ConnectionFactory connectionFactory, DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setPubSubDomain(true);

        factory.setSubscriptionDurable(true);
        ((CachingConnectionFactory) connectionFactory).setClientId("blog");

        configurer.configure(factory, connectionFactory);
        return factory;
    }

    @Override
    public void configureJmsListeners(JmsListenerEndpointRegistrar registrar) {
        JmsListenerContainerFactory jmsListenerContainerFactory = jmsFactory(connectionFactory, configurer);
        registrar.setContainerFactory(jmsListenerContainerFactory);

        // todo refactor me!
        ((BrokerEventServiceImpl) brokerEventService).setRegistrar(registrar);
        brokerEvenListeners.forEach(brokerEventService::registerEventListener);
    }
}
