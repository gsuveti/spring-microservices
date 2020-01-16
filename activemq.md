# Spring Boot & ActiveMQ

---
# Run an activemq instance in docker

```

  activemq:
    image: "rmohr/activemq"
    ports:
      - "8161:8161"
      - "61616:61616"

```
docker-compose.yml

#

- 8161: admin UI
- 61616: tcp transport

---

# Config

---

Add the activemq tcp url in application.yml.

```
spring:
  activemq:
    broker-url: tcp://localhost:61616
```
---

Create and configure a DefaultJmsListenerContainerFactoryConfigurer

```$xslt
    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(SingleConnectionFactory connectionFactory, DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setPubSubDomain(false);
        connectionFactory.setClientId("blog");

        configurer.configure(factory, connectionFactory);
        return factory;
    }
```


For durable subscriptions the connectionFactory needs a client ID.

---

Implement JmsListenerConfigurer and use the JmsListenerEndpointRegistrar to register endpoints

```$xslt
@Override
public void configureJmsListeners(JmsListenerEndpointRegistrar registrar) {

    brokerEvenListeners.forEach(brokerEventListener -> {
        String eventName = brokerEventListener.evenName();
        String subscriptionName = brokerEventListener.subscriptionName();
        Class eventClass = brokerEventListener.eventClass();

        SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();

        endpoint.setId("id_" + subscriptionName + eventName + UUID.randomUUID());
        endpoint.setDestination("Consumer." + subscriptionName + ".VirtualTopic." + eventName);
        endpoint.setSubscription(subscriptionName);

        endpoint.setMessageListener(message -> {
            try {
                Map<String, Object> map = ((ActiveMQMapMessage) message).getContentMap();
                UTF8Buffer payloadBuffer = (UTF8Buffer) map.get(PAYLOAD);
                brokerEventListener.onBrokerEvent(jacksonObjectMapper.readValue(payloadBuffer.getData(), eventClass));
            } catch (IOException | JMSException e) {
                e.printStackTrace();
            }
        });

        registrar.registerEndpoint(endpoint);

    });
}
```

---

Event publishing

```$xslt
@Override
public void publishEvent(BrokerEvent event) throws IOException {
    Map<String, String> actionMap = new HashMap<>();
    actionMap.put(ID, UUID.randomUUID().toString());
    actionMap.put(PAYLOAD, new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(event.getPayload()));

    String destination = "VirtualTopic." + event.getEventName();
    this.jmsTemplate.convertAndSend(new ActiveMQTopic(destination), actionMap);
}
```

## Virtual Topics 

The idea behind virtual topics is that producers send to a topic in the usual JMS way. Consumers can continue to use the Topic semantics in the JMS specification. However if the topic is virtual, consumer can consume from a physical queue for a logical topic subscription, allowing many consumers to be running on many machines & threads to load balance the load.

E.g., let’s say we have a topic called VirtualTopic.Orders. (Where the prefix VirtualTopic. indicates its a virtual topic). And we logically want to send orders to systems A and B. Now with regular durable topics we’d create a JMS consumer for clientID_A and “A” along with clientID_B and “B”.

With virtual topics we can just go right ahead and consume to queue Consumer.A.VirtualTopic.Orders to be a consumer for system A or consume to Consumer.B.VirtualTopic.Orders to be a consumer for system B.

---
## Demo

Start Blog and Shop applications and update one post.

```$xslt
curl -X PATCH \
  http://localhost:8083/api/posts/1 \
  -H 'Accept: */*' \
  -H 'Accept-Encoding: gzip, deflate' \
  -H 'Cache-Control: no-cache' \
  -H 'Connection: keep-alive' \
  -H 'Content-Length: 22' \
  -H 'Content-Type: application/json' \
  -H 'Host: localhost:8083' \
  -H 'Postman-Token: 984e91cf-2151-43df-9fcb-9d4af752353e,74e887b5-3f2b-4c45-b2a9-cddf9927cca7' \
  -H 'User-Agent: PostmanRuntime/7.20.1' \
  -H 'cache-control: no-cache' \
  -d '{
    "user": "George"
}'
```

Both Blog and Shop apps should be notified about the change.

---

## References


- https://tuhrig.de/virtual-topics-in-activemq/
- https://activemq.apache.org/virtual-destinations
- https://activemq.apache.org/what-is-the-difference-between-persistent-and-non-persistent-delivery
- https://activemq.apache.org/how-do-durable-queues-and-topics-work
