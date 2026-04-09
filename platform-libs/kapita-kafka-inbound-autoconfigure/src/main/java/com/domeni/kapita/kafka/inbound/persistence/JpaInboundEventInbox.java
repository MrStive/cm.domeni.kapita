package com.domeni.kapita.kafka.inbound.persistence;

import com.domeni.kapita.kafka.inbound.InboundEventInbox;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;

@RequiredArgsConstructor
public class JpaInboundEventInbox implements InboundEventInbox {

  private final InboxEventSpringRepository inboxEventSpringRepository;

  @Override
  public boolean markProcessingIfNew(String eventId, String eventType) {
    try {
      inboxEventSpringRepository.saveAndFlush(
          new InboxEvent(
              eventId, eventType, InboxEventStatus.PROCESSING, LocalDateTime.now(), null));
      return true;
    } catch (DataIntegrityViolationException exception) {
      return false;
    }
  }

  @Override
  public void markProcessed(String eventId) {
    inboxEventSpringRepository
        .findById(eventId)
        .map(InboxEvent::processed)
        .ifPresent(inboxEventSpringRepository::save);
  }
}
