package com.domeni.kapita.kafka.inbound;

public interface InboundEventHandler<T> {

  void handle(T payload, InboundEventContext context);

  Class<T> payloadType();

  String eventType();
}
