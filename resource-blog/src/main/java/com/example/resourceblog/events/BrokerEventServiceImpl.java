package com.example.resourceblog.events;

import com.example.resourceblog.messagebroker.api.BrokerEvent;
import com.example.resourceblog.messagebroker.api.BrokerEventListener;
import com.example.resourceblog.messagebroker.api.BrokerEventService;
import com.example.resourceblog.messagebroker.api.PreSendMessageProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.jms.core.JmsTemplate;

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


    public BrokerEventServiceImpl(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public void publishEvent(BrokerEvent event) throws IOException {
        Map<String, String> actionMap = new HashMap<>();
        actionMap.put(ID, UUID.randomUUID().toString());
        actionMap.put(PAYLOAD, new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(event.getPayload()));

        String destination = "VirtualTopic." + event.getEventName();
        this.jmsTemplate.convertAndSend(new ActiveMQTopic(destination), actionMap);
    }

    @Override
    public CompletableFuture<Void> registerEventListener(BrokerEventListener brokerEventListener) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void registerPreSendMessageProcessor(PreSendMessageProcessor preSendMessageProcessor) {
        log.warning("Received message processor, however the current implementation does not support message processors");
    }
}
