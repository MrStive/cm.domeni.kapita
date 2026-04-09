package com.domeni.kapita.kafka.inbound;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.domeni.kapita.kafka.inbound.persistence.InboxEvent;
import com.domeni.kapita.kafka.inbound.persistence.InboxEventSpringRepository;
import com.domeni.kapita.kafka.inbound.persistence.JpaInboundEventInbox;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class JpaInboundEventInboxTest {

  @Mock private InboxEventSpringRepository inboxEventSpringRepository;

  @InjectMocks private JpaInboundEventInbox objectUnderTest;

  @Test
  void markProcessingIfNewShouldPersistEventTest() {
    boolean result = objectUnderTest.markProcessingIfNew("event-1", "USER_CREATED");

    ArgumentCaptor<InboxEvent> eventCaptor = ArgumentCaptor.forClass(InboxEvent.class);
    then(inboxEventSpringRepository).should().saveAndFlush(eventCaptor.capture());
    assertThat(result).isTrue();
    assertThat(eventCaptor.getValue().getId()).isEqualTo("event-1");
    assertThat(eventCaptor.getValue().getType()).isEqualTo("USER_CREATED");
  }

  @Test
  void markProcessingIfNewShouldReturnFalseWhenEventAlreadyExistsTest() {
    org.mockito.Mockito.doThrow(new DataIntegrityViolationException("duplicate"))
        .when(inboxEventSpringRepository)
        .saveAndFlush(org.mockito.ArgumentMatchers.any(InboxEvent.class));

    boolean result = objectUnderTest.markProcessingIfNew("event-1", "USER_CREATED");

    assertThat(result).isFalse();
  }

  @Test
  void markProcessedShouldUpdatePersistedEventTest() {
    InboxEvent event = new InboxEvent();
    event.setId("event-1");
    given(inboxEventSpringRepository.findById("event-1")).willReturn(Optional.of(event));

    objectUnderTest.markProcessed("event-1");

    then(inboxEventSpringRepository).should().save(event);
    assertThat(event.getProcessedAt()).isNotNull();
  }
}
