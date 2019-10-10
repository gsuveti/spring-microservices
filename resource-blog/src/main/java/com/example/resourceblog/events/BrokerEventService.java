package com.example.resourceblog.events;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public interface BrokerEventService {

    public void publishEvent(BrokerEvent event) throws IOException;

    public CompletableFuture<Void> registerEventListener(BrokerEventListener brokerEvenListener);
}
