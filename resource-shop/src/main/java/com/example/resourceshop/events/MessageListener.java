package com.example.resourceshop.events;

import com.example.resourceshop.ResourceShopApplication;
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
            subscription = "shop_durable_topic")
    public void receiveMessages(Map<String, String> message) {
        log.info(message.get("id"));
        log.info(message.get("payload"));
    }
}
