package com.example.resourceshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class ResourceShopApplication {

    public static final String SUBSCRIPTION_NAME = "SHOP_SUBSCRIPTION";
    public static final String DESTINATION_PREFIX = "Consumer.SHOP_SUBSCRIPTION.VirtualTopic.";
    public static final String POSTS_MESSAGE_QUEUE = "POSTS_MESSAGE_QUEUE";

    public static void main(String[] args) {
        SpringApplication.run(ResourceShopApplication.class, args);
    }

}
