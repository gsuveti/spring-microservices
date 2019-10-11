package com.example.resourceshop.events;

import com.example.resourceshop.ResourceShopApplication;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.logging.Logger;

@Component
public class MessageListener {
    private static final Logger log = Logger.getLogger(MessageListener.class.toString());

    @JmsListener(
            destination = ResourceShopApplication.POSTS_MESSAGE_QUEUE,
            containerFactory = "jmsFactory",
            subscription = "shop_subscription")
    public void receiveMessages(Map<String, String> message) {
        try {
            log.info(new ObjectMapper().writeValueAsString(message));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
