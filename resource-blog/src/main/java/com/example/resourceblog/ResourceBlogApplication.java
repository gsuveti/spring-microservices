package com.example.resourceblog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.connection.SingleConnectionFactory;

import javax.jms.ConnectionFactory;

@SpringBootApplication
@EnableJpaRepositories
@EnableJms
public class ResourceBlogApplication {

    public static final String POSTS_MESSAGE_QUEUE = "POSTS_MESSAGE_QUEUE";


    @Bean
    public JmsListenerContainerFactory<?> jmsFactory(ConnectionFactory connectionFactory, DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setPubSubDomain(true);

        factory.setSubscriptionDurable(true);
        ((CachingConnectionFactory)connectionFactory).setClientId("blog");

        configurer.configure(factory, connectionFactory);
        return factory;
    }

    public static void main(String[] args) {
        SpringApplication.run(ResourceBlogApplication.class, args);
    }

}
