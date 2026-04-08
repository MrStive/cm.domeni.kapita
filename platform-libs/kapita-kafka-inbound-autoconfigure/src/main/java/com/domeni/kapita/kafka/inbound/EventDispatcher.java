package com.domeni.kapita.kafka.inbound;

public interface EventDispatcher {

  void dispatch(byte[] raw, InboundEventContext context);
}
