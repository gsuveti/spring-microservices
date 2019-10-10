package com.example.resourceblog.events;

import com.example.resourceblog.messagebroker.api.BrokerEvent;
import com.example.resourceblog.messagebroker.api.BrokerEventListener;
import com.example.resourceblog.messagebroker.api.BrokerEventService;
import com.example.resourceblog.messagebroker.api.PreSendMessageProcessor;
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
    private static final String PAYLOAD = "PAYLOAD";
    private static final String ID = "ID";

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
        actionMap.put(ID, UUID.randomUUID().toString());
        actionMap.put(PAYLOAD, new ObjectMapper().writeValueAsString(event.getPayload()));

        this.jmsTemplate.convertAndSend(event.getEventName(), actionMap);
    }

    @Override
    public CompletableFuture<Void> registerEventListener(BrokerEventListener brokerEventListener) {
        if (registrar != null) {
            String eventName = brokerEventListener.evenName();
            String subscriptionName = brokerEventListener.subscriptionName();
            Class eventClass = brokerEventListener.eventClass();

            SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();

            endpoint.setId("id_" + subscriptionName + eventName + UUID.randomUUID());
            endpoint.setDestination(eventName);
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
        }

        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void registerPreSendMessageProcessor(PreSendMessageProcessor preSendMessageProcessor) {
        log.warning("Received message processor, however the current implementation does not support message processors");
    }
}
