package com.domeni.kapita.kafka.inbound;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultInboundEventEnvelopeReader implements InboundEventEnvelopeReader {

  private final ObjectMapper objectMapper;

  @Override
  public InboundEventEnvelope<JsonNode> read(byte[] raw) {
    try {
      return objectMapper.readValue(raw, new TypeReference<InboundEventEnvelope<JsonNode>>() {});
    } catch (IOException exception) {
      throw new IllegalArgumentException("invalid inbound event payload", exception);
    }
  }
}
