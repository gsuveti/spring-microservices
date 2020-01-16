package com.example.resourceblog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class ResourceBlogApplication {

    public static final String POSTS_MESSAGE_QUEUE = "POSTS_MESSAGE_QUEUE";
    public static final String PAYLOAD = "PAYLOAD";
    public static final String ID = "ID";


    public static void main(String[] args) {
        SpringApplication.run(ResourceBlogApplication.class, args);
    }

}

