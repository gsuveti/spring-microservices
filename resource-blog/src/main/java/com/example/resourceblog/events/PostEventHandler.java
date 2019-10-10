package com.example.resourceblog.events;

import com.example.resourceblog.domain.Post;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RepositoryEventHandler
public class PostEventHandler {
    private final JmsTemplate jmsTemplate;
    private final BrokerEventService brokerEventService;

    public PostEventHandler(JmsTemplate jmsTemplate, BrokerEventService brokerEventService) {
        this.jmsTemplate = jmsTemplate;
        this.brokerEventService = brokerEventService;
    }

    @HandleAfterSave
    public void onAfterSave(Post post) throws IOException {
        brokerEventService.publishEvent(new PostEvent(post));
    }
}
