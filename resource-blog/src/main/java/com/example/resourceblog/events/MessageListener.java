package com.example.resourceblog.events;

import com.example.resourceblog.ResourceBlogApplication;
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
            destination = ResourceBlogApplication.POSTS_MESSAGE_QUEUE,
            containerFactory = "jmsListenerContainerFactory",
            subscription = "blog_subscription_3")
    public void receiveMessages(Map<String, String> message) {
        try {
            log.info(new ObjectMapper().writeValueAsString(message));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
