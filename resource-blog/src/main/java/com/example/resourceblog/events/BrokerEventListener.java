package com.example.resourceblog.events;

public interface BrokerEventListener<E> {

    public void onBrokerEvent(E event);

    public String evenName();

    public String subscriptionName();

    public Class<E> eventClass();
}
