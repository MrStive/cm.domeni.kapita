package com.domeni.kapita.kafka.inbound;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
public class DefaultEventDispatcher implements EventDispatcher {

  private final ObjectMapper objectMapper;
  private final InboundEventEnvelopeReader envelopeReader;
  private final Map<String, InboundEventHandler<?>> handlersByType;
  private final InboundEventInbox inboundEventInbox;

  public DefaultEventDispatcher(
      ObjectMapper objectMapper,
      InboundEventEnvelopeReader envelopeReader,
      List<InboundEventHandler<?>> handlers,
      InboundEventInbox inboundEventInbox) {
    this.objectMapper = objectMapper;
    this.envelopeReader = envelopeReader;
    this.handlersByType = indexHandlers(handlers);
    this.inboundEventInbox = inboundEventInbox;
  }

  @Transactional
  @Override
  public void dispatch(byte[] raw, InboundEventContext context) {
    InboundEventEnvelope<JsonNode> envelope = requireEnvelope(envelopeReader.read(raw));
    String eventId = envelope.getEventId();
    String eventType = envelope.getEventType();
    InboundEventHandler<?> handler = findHandler(eventType);
    Object payload = parsePayload(envelope.getPayload(), handler.payloadType(), eventType);

    log.debug(
        "Received inbound event type={} eventId={} topic={} partition={} offset={}",
        eventType,
        eventId,
        context.topic(),
        context.partition(),
        context.offset());

    if (!inboundEventInbox.markProcessingIfNew(eventId, eventType)) {
      log.info("Skipping duplicate inbound event type={} eventId={}", eventType, eventId);
      return;
    }

    invokeHandler(handler, payload, context);
    inboundEventInbox.markProcessed(eventId);
  }

  private static InboundEventEnvelope<JsonNode> requireEnvelope(InboundEventEnvelope<JsonNode> envelope) {
    if (envelope.getEventId() == null || envelope.getEventId().isBlank()) {
      throw new IllegalArgumentException("inbound event id is required");
    }
    if (envelope.getEventType() == null || envelope.getEventType().isBlank()) {
      throw new IllegalArgumentException("inbound event type is required");
    }
    if (envelope.getPayload() == null || envelope.getPayload().isNull()) {
      throw new IllegalArgumentException("inbound event payload is required");
    }
    return envelope;
  }

  private static Map<String, InboundEventHandler<?>> indexHandlers(List<InboundEventHandler<?>> handlers) {
    Map<String, InboundEventHandler<?>> indexedHandlers = new LinkedHashMap<>();
    handlers.forEach(
        handler -> {
          InboundEventHandler<?> previous = indexedHandlers.putIfAbsent(handler.eventType(), handler);
          if (previous != null) {
            throw new IllegalStateException(
                "multiple inbound handlers registered for event type: " + handler.eventType());
          }
        });
    return Map.copyOf(indexedHandlers);
  }

  private InboundEventHandler<?> findHandler(String eventType) {
    InboundEventHandler<?> handler = handlersByType.get(eventType);
    if (handler == null) {
      throw new IllegalArgumentException("unsupported inbound event type: %s".formatted(eventType));
    }
    return handler;
  }

  private Object parsePayload(JsonNode payload, Class<?> payloadType, String eventType) {
    try {
      return objectMapper.treeToValue(payload, payloadType);
    } catch (JsonProcessingException exception) {
      throw new IllegalArgumentException(
          "invalid payload for inbound event type: %s".formatted(eventType), exception);
    }
  }

  @SuppressWarnings("unchecked")
  private static void invokeHandler(
      InboundEventHandler<?> handler, Object payload, InboundEventContext context) {
    ((InboundEventHandler<Object>) handler).handle(payload, context);
  }
}
