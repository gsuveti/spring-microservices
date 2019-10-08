package com.example.resourceblog.events;

import com.example.resourceblog.ResourceBlogApplication;
import com.example.resourceblog.domain.Post;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RepositoryEventHandler
public class PostEventHandler {
    private final JmsTemplate jmsTemplate;

    public PostEventHandler(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @HandleAfterSave
    public void onAfterSave(Post post) throws IOException {
        Map<String, String> actionMap = new HashMap<>();
        actionMap.put("id", UUID.randomUUID().toString());
        actionMap.put("payload", new ObjectMapper().writeValueAsString(post));
        this.jmsTemplate.convertAndSend(ResourceBlogApplication.POSTS_MESSAGE_QUEUE, actionMap);
    }
}
