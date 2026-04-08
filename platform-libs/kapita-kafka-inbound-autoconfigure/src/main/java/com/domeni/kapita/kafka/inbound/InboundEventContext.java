package com.domeni.kapita.kafka.inbound;

public record InboundEventContext(String topic, int partition, long offset) {}
