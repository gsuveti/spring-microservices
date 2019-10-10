package com.example.resourceblog.config;

import com.example.resourceblog.messagebroker.api.BrokerEventService;
import com.example.resourceblog.events.BrokerEventServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

@Configuration
public class BrokerEventServiceConfig {
    private final ObjectMapper jacksonObjectMapper;

    public BrokerEventServiceConfig(ObjectMapper jacksonObjectMapper) {
        this.jacksonObjectMapper = jacksonObjectMapper;
    }


    @Bean
    public BrokerEventService brokerEventService(JmsTemplate jmsTemplate) {
        return new BrokerEventServiceImpl(jmsTemplate, jacksonObjectMapper);
    }
}
