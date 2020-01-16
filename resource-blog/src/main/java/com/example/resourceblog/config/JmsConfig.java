package com.example.resourceblog.config;

import com.example.resourceblog.messagebroker.api.BrokerEventListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;
import org.springframework.jms.connection.SingleConnectionFactory;

import javax.jms.JMSException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import static com.example.resourceblog.ResourceBlogApplication.PAYLOAD;

@Configuration
@EnableJms
public class JmsConfig implements JmsListenerConfigurer {

    private static final Logger log = Logger.getLogger(JmsConfig.class.toString());


    private final ObjectMapper jacksonObjectMapper;

    @Autowired(required = false)
    private final List<BrokerEventListener> brokerEvenListeners;


    public JmsConfig(ObjectMapper jacksonObjectMapper, List<BrokerEventListener> brokerEvenListeners) {

        this.jacksonObjectMapper = jacksonObjectMapper;
        this.brokerEvenListeners = brokerEvenListeners;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(SingleConnectionFactory connectionFactory, DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setPubSubDomain(false);
        connectionFactory.setClientId("blog");

        configurer.configure(factory, connectionFactory);
        return factory;
    }

    @Override
    public void configureJmsListeners(JmsListenerEndpointRegistrar registrar) {

        brokerEvenListeners.forEach(brokerEventListener -> {
            String eventName = brokerEventListener.evenName();
            String subscriptionName = brokerEventListener.subscriptionName();
            Class eventClass = brokerEventListener.eventClass();

            SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();

            endpoint.setId("id_" + subscriptionName + eventName + UUID.randomUUID());
            endpoint.setDestination("Consumer." + subscriptionName + ".VirtualTopic." + eventName);
            endpoint.setSubscription(subscriptionName);

            endpoint.setMessageListener(message -> {
                try {
                    Map<String, Object> map = ((ActiveMQMapMessage) message).getContentMap();
                    UTF8Buffer payloadBuffer = (UTF8Buffer) map.get(PAYLOAD);
                    brokerEventListener.onBrokerEvent(jacksonObjectMapper.readValue(payloadBuffer.getData(), eventClass));
                } catch (IOException | JMSException e) {
                    e.printStackTrace();
                }
            });

            registrar.registerEndpoint(endpoint);

        });
    }
}
