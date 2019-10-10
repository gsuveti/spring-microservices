package com.example.resourceblog.messagebroker.api;

public interface PreSendMessageProcessor {
    boolean acceptsMessage(Object message);
    void processMessage(Object message);
}
