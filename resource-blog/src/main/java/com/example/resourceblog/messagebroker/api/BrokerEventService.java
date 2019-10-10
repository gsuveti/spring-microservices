package com.example.resourceblog.messagebroker.api;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public interface BrokerEventService {

    public void publishEvent(BrokerEvent event) throws IOException;

    public CompletableFuture<Void> registerEventListener(BrokerEventListener brokerEvenListener);

    public void registerPreSendMessageProcessor(PreSendMessageProcessor preSendMessageProcessor);
}
