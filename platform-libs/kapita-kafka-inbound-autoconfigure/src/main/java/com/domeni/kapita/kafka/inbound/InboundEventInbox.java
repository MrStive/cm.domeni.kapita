package com.domeni.kapita.kafka.inbound;

public interface InboundEventInbox {

  boolean markProcessingIfNew(String eventId, String eventType);

  void markProcessed(String eventId);
}
