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
<p style="text-align:end">docker-compose.yml</p>


- 8161: admin UI
- 61616: tcp transport

---

# Config

---

Add the activemq tcp url in aplication.yml and configure JMS to use publish-subscribe domain(topics) instead of point-to-point domain(queues).

```
spring:
  activemq:
    broker-url: tcp://localhost:61616
  jms:
    pub-sub-domain: true
```
---

Create and configure a DefaultJmsListenerContainerFactoryConfigurer

<pre style="font-size:70%">
 @Bean
  public JmsListenerContainerFactory<?> jmsListenerContainerFactory(
   SingleConnectionFactory connectionFactory,
   DefaultJmsListenerContainerFactoryConfigurer configurer) {
      
      DefaultJmsListenerContainerFactory factory = 
      				new DefaultJmsListenerContainerFactory();
      factory.setPubSubDomain(true);
      factory.setSubscriptionDurable(true);
      
      connectionFactory.setClientId("blog");

      configurer.configure(factory, connectionFactory);
      return factory;
  }

</pre>

For durable subscriptions the connectionFactory needs a client ID.

---

Implement JmsListenerConfigurer and use the JmsListenerEndpointRegistrar to register endpoints

<pre style="font-size:70%">
public class JmsConfig implements JmsListenerConfigurer {
...
@Override
public void configureJmsListeners(JmsListenerEndpointRegistrar registrar) {
    JmsListenerContainerFactory jmsListenerContainerFactory = 
    				jmsListenerContainerFactory(connectionFactory, configurer);
    registrar.setContainerFactory(jmsListenerContainerFactory);

    ((BrokerEventServiceImpl) brokerEventService).setRegistrar(registrar);
    brokerEvenListeners.forEach(brokerEventService::registerEventListener);
}

}
</pre>


---

Endpoint registration

<pre style="font-size:70%">

SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();

endpoint.setId("id_" + subscriptionName + eventName + UUID.randomUUID());
endpoint.setDestination(eventName);
endpoint.setSubscription(subscriptionName);

endpoint.setMessageListener(message -> {
    try {
        Map<String, Object> map = ((ActiveMQMapMessage) message).getContentMap();
        UTF8Buffer payloadBuffer = (UTF8Buffer) map.get(PAYLOAD);
        brokerEventListener.onBrokerEvent(
        	jacksonObjectMapper.readValue(
            		payloadBuffer.getData(), eventClass
                )
        );
    } catch (IOException | JMSException e) {
        e.printStackTrace();
    }
});

registrar.registerEndpoint(endpoint);
</pre>

---

Event publishing

<pre style="font-size:70%">

public class BrokerEventServiceImpl implements BrokerEventService {
 private final JmsTemplate jmsTemplate;
 @Override
    public void publishEvent(BrokerEvent event) throws IOException {
        Map<String, String> actionMap = new HashMap<>();
        actionMap.put(ID, UUID.randomUUID().toString());
        actionMap.put(
            PAYLOAD, 
            new ObjectMapper().writeValueAsString(event.getPayload())
        );

        this.jmsTemplate.convertAndSend(event.getEventName(), actionMap);
    }
}

</pre>




