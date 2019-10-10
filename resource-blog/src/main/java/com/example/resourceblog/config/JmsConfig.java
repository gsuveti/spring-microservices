package com.example.resourceblog.config;

import com.example.resourceblog.events.BrokerEventServiceImpl;
import com.example.resourceblog.messagebroker.api.BrokerEventListener;
import com.example.resourceblog.messagebroker.api.BrokerEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.connection.SingleConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.context.support.GenericWebApplicationContext;

import java.util.List;
import java.util.logging.Logger;

@Configuration
@EnableJms
public class JmsConfig implements JmsListenerConfigurer {
    private static final Logger log = Logger.getLogger(JmsConfig.class.toString());

    private final SingleConnectionFactory connectionFactory;
    private final DefaultJmsListenerContainerFactoryConfigurer configurer;
    private final JmsTemplate jmsTemplate;
    private final BrokerEventService brokerEventService;

    @Autowired(required = false)
    private final List<BrokerEventListener> brokerEvenListeners;

    @Autowired
    private GenericWebApplicationContext context;

    public JmsConfig(SingleConnectionFactory connectionFactory, DefaultJmsListenerContainerFactoryConfigurer configurer, JmsTemplate jmsTemplate, BrokerEventService brokerEventService, List<BrokerEventListener> brokerEvenListeners) {
        this.connectionFactory = connectionFactory;
        this.configurer = configurer;
        this.jmsTemplate = jmsTemplate;
        this.brokerEventService = brokerEventService;
        this.brokerEvenListeners = brokerEvenListeners;
    }

    @Bean
    public JmsListenerContainerFactory<?> jmsListenerContainerFactory(SingleConnectionFactory connectionFactory, DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setPubSubDomain(true);

        factory.setSubscriptionDurable(true);
        connectionFactory.setClientId("blog");

        configurer.configure(factory, connectionFactory);
        return factory;
    }

    @Override
    public void configureJmsListeners(JmsListenerEndpointRegistrar registrar) {
        JmsListenerContainerFactory jmsListenerContainerFactory = jmsListenerContainerFactory(connectionFactory, configurer);
        registrar.setContainerFactory(jmsListenerContainerFactory);

        // todo refactor me!
        ((BrokerEventServiceImpl) brokerEventService).setRegistrar(registrar);
        brokerEvenListeners.forEach(brokerEventService::registerEventListener);
    }
}
