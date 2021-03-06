package com.example.resourceblog.config;

import com.example.resourceblog.events.BrokerEventServiceImpl;
import com.example.resourceblog.messagebroker.api.BrokerEventService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

@Configuration
public class BrokerEventServiceConfig {

    public BrokerEventServiceConfig() {
    }


    @Bean
    public BrokerEventService brokerEventService(JmsTemplate jmsTemplate) {
        return new BrokerEventServiceImpl(jmsTemplate);
    }
}
