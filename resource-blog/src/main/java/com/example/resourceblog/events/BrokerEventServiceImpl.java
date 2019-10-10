package com.example.resourceblog.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.JMSException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class BrokerEventServiceImpl implements BrokerEventService {
    private static final Logger log = Logger.getLogger(BrokerEventServiceImpl.class.toString());
    private final JmsTemplate jmsTemplate;
    private final ObjectMapper jacksonObjectMapper;

    private JmsListenerEndpointRegistrar registrar;

    public BrokerEventServiceImpl(JmsTemplate jmsTemplate, ObjectMapper jacksonObjectMapper) {
        this.jmsTemplate = jmsTemplate;
        this.jacksonObjectMapper = jacksonObjectMapper;
    }

    public void setRegistrar(JmsListenerEndpointRegistrar registrar) {
        this.registrar = registrar;
    }

    @Override
    public void publishEvent(BrokerEvent event) throws IOException {
        Map<String, String> actionMap = new HashMap<>();
        actionMap.put("id", UUID.randomUUID().toString());
        actionMap.put("payload", new ObjectMapper().writeValueAsString(event.getPayload()));

        this.jmsTemplate.convertAndSend(event.getEventName(), actionMap);
    }

    @Override
    public CompletableFuture<Void> registerEventListener(BrokerEventListener brokerEventListener) {
        if (registrar != null) {
            String eventName = brokerEventListener.evenName();
            String subscriptionName = brokerEventListener.subscriptionName();
            String className = brokerEventListener.getClass().getName();

            SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();

            endpoint.setId("id_" + subscriptionName + eventName + UUID.randomUUID());
            endpoint.setDestination(eventName);
            endpoint.setSubscription(subscriptionName);

            endpoint.setMessageListener(message -> {
                try {
                    Map<String, Object> map = ((ActiveMQMapMessage) message).getContentMap();
                    UTF8Buffer payloadBuffer = (UTF8Buffer) map.get("payload");
                    brokerEventListener.onBrokerEvent(jacksonObjectMapper.readValue(payloadBuffer.getData(), brokerEventListener.eventClass()));
                } catch (IOException | JMSException e) {
                    e.printStackTrace();
                }
            });

            registrar.registerEndpoint(endpoint);
        }

        return CompletableFuture.completedFuture(null);
    }
}
