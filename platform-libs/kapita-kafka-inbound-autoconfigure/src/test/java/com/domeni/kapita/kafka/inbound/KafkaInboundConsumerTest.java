package com.domeni.kapita.kafka.inbound;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

@ExtendWith(MockitoExtension.class)
class KafkaInboundConsumerTest {

  @Mock private EventDispatcher eventDispatcher;
  @Mock private Acknowledgment acknowledgment;

  @InjectMocks private KafkaInboundConsumer objectUnderTest;

  @Test
  void consumeShouldDispatchAndAckTest() {
    byte[] raw = "{}".getBytes();

    objectUnderTest.consume(raw, "user-created", 0, 10L, acknowledgment);

    then(eventDispatcher).should().dispatch(raw, new InboundEventContext("user-created", 0, 10L));
    then(acknowledgment).should().acknowledge();
  }

  @Test
  void consumeShouldNotAckWhenDispatchFailsTest() {
    byte[] raw = "{}".getBytes();
    org.mockito.Mockito.doThrow(new RuntimeException("boom"))
        .when(eventDispatcher)
        .dispatch(raw, new InboundEventContext("user-created", 0, 10L));

    assertThrows(
        RuntimeException.class,
        () -> objectUnderTest.consume(raw, "user-created", 0, 10L, acknowledgment));

    verifyNoInteractions(acknowledgment);
  }
}
