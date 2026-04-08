package com.domeni.kapita.service.events.kafka.consumer;

import cm.lao.generated.domeni.kapita.event.dto.DomainEventType;
import cm.lao.generated.domeni.kapita.event.dto.UserCreatedEventDTO;
import cm.lao.generated.domeni.kapita.event.dto.UserCreatedEventEnvelopeDTO;
import com.domeni.kapita.service.UserService;
import com.domeni.kapita.service.mapper.UserEventMapper;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCreatedEventConsumer {

  private final UserService userService;
  private final UserEventMapper userEventMapper;

  @KafkaListener(
      topics = "${kapita.messaging.user-created.topic:user-created}",
      groupId = "${spring.kafka.consumer.group-id:kapita-service}",
      containerFactory = "userCreatedEventKafkaListenerContainerFactory")
  public void handleUserCreatedEvent(
      @Payload UserCreatedEventEnvelopeDTO event,
      @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
      @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
      @Header(KafkaHeaders.OFFSET) long offset,
      Acknowledgment acknowledgment) {

    UserCreatedEventDTO payload = validateEvent(event);
    log.info(
        "Received UserCreatedEvent from topic: {}, partition: {}, offset: {}, userId: {}",
        topic,
        partition,
        offset,
        payload.getId());

    try {
      userService.createUser(userEventMapper.map(payload));
      log.info("Successfully processed UserCreatedEvent for userId: {}", payload.getId());
      acknowledgment.acknowledge();
    } catch (Exception exception) {
      log.error("Failed to process UserCreatedEvent for userId: {}", payload.getId(), exception);
      throw new RuntimeException("Failed to process UserCreatedEvent", exception);
    }
  }

  private UserCreatedEventDTO validateEvent(UserCreatedEventEnvelopeDTO event) {
    if (event == null) {
      throw new IllegalArgumentException("user created event payload is required");
    }
    if (!Objects.equals(event.getEventType(), DomainEventType.USER_CREATED)) {
      throw new IllegalArgumentException("unsupported event type for user creation consumer");
    }
    return event.getPayload();
  }
}
