package com.example.resourceblog.messagebroker.api;

public interface BrokerEvent<T> {

    public T getPayload();

    public String getEventName();

}
