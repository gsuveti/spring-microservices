package com.example.resourceblog.events;

import com.example.resourceblog.ResourceBlogApplication;
import com.example.resourceblog.domain.Post;
import javafx.geometry.Pos;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.logging.Logger;

@Component
public class PostEventListener implements BrokerEventListener<Post> {
    private static final Logger log = Logger.getLogger(PostEventListener.class.toString());

    @Override
    public void onBrokerEvent(Post post) {
        try {
            log.info(new ObjectMapper().writeValueAsString(post));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String evenName() {
        return ResourceBlogApplication.POSTS_MESSAGE_QUEUE;
    }

    @Override
    public String subscriptionName() {
        return "blog_subscription";
    }

    @Override
    public Class<Post> eventClass() {
        return Post.class;
    }
}
