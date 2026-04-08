package com.domeni.kapita.kafka.inbound;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultEventDispatcherTest {

  @Mock private InboundEventHandler<TestPayload> testPayloadInboundEventHandler;
  @Mock private InboundEventInbox inboundEventInbox;

  private DefaultEventDispatcher objectUnderTest;

  @BeforeEach
  void setUp() {
    lenient().when(testPayloadInboundEventHandler.eventType()).thenReturn("USER_CREATED");
    lenient().when(testPayloadInboundEventHandler.payloadType()).thenReturn(TestPayload.class);
    objectUnderTest =
        new DefaultEventDispatcher(
            new ObjectMapper(),
            new DefaultInboundEventEnvelopeReader(new ObjectMapper()),
            List.of(testPayloadInboundEventHandler),
            inboundEventInbox);
    clearInvocations(testPayloadInboundEventHandler);
  }

  @Test
  void dispatchShouldRouteEventToMatchingHandlerTest() {
    byte[] raw =
        """
        {
          "eventId": "event-1",
          "eventType": "USER_CREATED",
          "payload": {
            "id": "user-1",
            "name": "john.doe"
          }
        }
        """
            .getBytes(StandardCharsets.UTF_8);
    InboundEventContext context = new InboundEventContext("user-created", 0, 10L);

    given(inboundEventInbox.markProcessingIfNew("event-1", "USER_CREATED")).willReturn(true);

    objectUnderTest.dispatch(raw, context);

    ArgumentCaptor<TestPayload> payloadCaptor = ArgumentCaptor.forClass(TestPayload.class);
    verify(testPayloadInboundEventHandler).handle(payloadCaptor.capture(), eq(context));
    verify(inboundEventInbox).markProcessed("event-1");
    assertThat(payloadCaptor.getValue().id()).isEqualTo("user-1");
    assertThat(payloadCaptor.getValue().name()).isEqualTo("john.doe");
  }

  @Test
  void dispatchShouldSkipDuplicateEventTest() {
    byte[] raw =
        """
        {
          "eventId": "event-1",
          "eventType": "USER_CREATED",
          "payload": {
            "id": "user-1",
            "name": "john.doe"
          }
        }
        """
            .getBytes(StandardCharsets.UTF_8);

    given(inboundEventInbox.markProcessingIfNew("event-1", "USER_CREATED")).willReturn(false);

    objectUnderTest.dispatch(raw, new InboundEventContext("user-created", 0, 10L));

    verify(inboundEventInbox).markProcessingIfNew("event-1", "USER_CREATED");
    verify(testPayloadInboundEventHandler).payloadType();
    verifyNoMoreInteractions(testPayloadInboundEventHandler);
    verifyNoMoreInteractions(inboundEventInbox);
  }

  @Test
  void dispatchShouldRejectUnsupportedEventTypeTest() {
    byte[] raw =
        """
        {
          "eventId": "event-1",
          "eventType": "USER_UPDATED",
          "payload": {
            "id": "user-1"
          }
        }
        """
            .getBytes(StandardCharsets.UTF_8);

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> objectUnderTest.dispatch(raw, new InboundEventContext("user-created", 0, 10L)));

    assertThat(exception).hasMessageContaining("unsupported inbound event type");
    verifyNoInteractions(inboundEventInbox);
  }

  @Test
  void dispatchShouldRejectMissingPayloadTest() {
    byte[] raw =
        """
        {
          "eventId": "event-1",
          "eventType": "USER_CREATED",
          "payload": null
        }
        """
            .getBytes(StandardCharsets.UTF_8);

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> objectUnderTest.dispatch(raw, new InboundEventContext("user-created", 0, 10L)));

    assertThat(exception).hasMessageContaining("payload is required");
    verifyNoInteractions(testPayloadInboundEventHandler);
    verifyNoInteractions(inboundEventInbox);
  }

  @Test
  void dispatchShouldPropagateHandlerFailuresTest() {
    byte[] raw =
        """
        {
          "eventId": "event-1",
          "eventType": "USER_CREATED",
          "payload": {
            "id": "user-1",
            "name": "john.doe"
          }
        }
        """
            .getBytes(StandardCharsets.UTF_8);

    given(inboundEventInbox.markProcessingIfNew("event-1", "USER_CREATED")).willReturn(true);
    org.mockito.Mockito.doThrow(new RuntimeException("boom"))
        .when(testPayloadInboundEventHandler)
        .handle(any(TestPayload.class), any(InboundEventContext.class));

    RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> objectUnderTest.dispatch(raw, new InboundEventContext("user-created", 0, 10L)));

    assertThat(exception).hasMessageContaining("boom");
    verify(inboundEventInbox, never()).markProcessed("event-1");
  }

  private record TestPayload(String id, String name) {}
}
