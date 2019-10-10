package com.example.resourceblog.messagebroker.api;

public interface BrokerEventListener<E> {

    public void onBrokerEvent(E event);

    public String evenName();

    public String subscriptionName();

    public Class<E> eventClass();
}
