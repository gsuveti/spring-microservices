package com.example.resourceshop.events;

import com.example.resourceshop.ResourceShopApplication;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.logging.Logger;

@Component
public class SecondMessageListener {
    private static final Logger log = Logger.getLogger(SecondMessageListener.class.toString());

    @JmsListener(
            destination = ResourceShopApplication.DESTINATION_PREFIX + ResourceShopApplication.POSTS_MESSAGE_QUEUE,
            subscription = ResourceShopApplication.SUBSCRIPTION_NAME)
    public void receiveMessages(Map<String, String> message) {
        try {
            log.info(new ObjectMapper().writeValueAsString(message));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
