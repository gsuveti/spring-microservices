package com.example.resourceblog.events;

public interface BrokerEvent<T> {

    public T getPayload();

    public String getEventName();

}
