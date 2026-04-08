package com.domeni.kapita.kafka.inbound;

import com.fasterxml.jackson.databind.JsonNode;

public interface InboundEventEnvelopeReader {

  InboundEventEnvelope<JsonNode> read(byte[] raw);
}
