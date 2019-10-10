package com.example.resourceblog.events;

import com.example.resourceblog.ResourceBlogApplication;
import com.example.resourceblog.domain.Post;

public class PostEvent implements BrokerEvent<Post> {
    private Post post;

    public PostEvent(Post post) {
        this.post = post;
    }

    @Override
    public Post getPayload() {
        return post;
    }

    @Override
    public String getEventName() {
        return ResourceBlogApplication.POSTS_MESSAGE_QUEUE;
    }
}
