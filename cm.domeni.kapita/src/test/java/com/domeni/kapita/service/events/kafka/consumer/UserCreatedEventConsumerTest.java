package com.domeni.kapita.service.events.kafka.consumer;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import cm.lao.generated.domeni.kapita.event.dto.DomainEventType;
import cm.lao.generated.domeni.kapita.event.dto.UserCreatedEventDTO;
import cm.lao.generated.domeni.kapita.event.dto.UserCreatedEventEnvelopeDTO;
import com.domeni.kapita.domain.user.UserCreationData;
import com.domeni.kapita.service.UserService;
import com.domeni.kapita.service.mapper.UserEventMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

@ExtendWith(MockitoExtension.class)
class UserCreatedEventConsumerTest {

  @Mock private UserService userService;
  @Mock private UserEventMapper userEventMapper;
  @Mock private Acknowledgment acknowledgment;

  @InjectMocks private UserCreatedEventConsumer consumer;

  @Test
  void handleUserCreatedEventShouldCreateUserAndAckTest() {
    // Given
    UUID userId = UUID.randomUUID();
    UserCreatedEventDTO payload = new UserCreatedEventDTO().id(userId).username("john.doe");
    UserCreatedEventEnvelopeDTO event =
        new UserCreatedEventEnvelopeDTO().eventType(DomainEventType.USER_CREATED).payload(payload);
    UserCreationData mappedData = UserCreationData.builder().id(userId).name("john.doe").build();
    given(userEventMapper.map(payload)).willReturn(mappedData);

    // When
    consumer.handleUserCreatedEvent(event, "user-created", 0, 10L, acknowledgment);

    // Then
    org.mockito.Mockito.verify(userEventMapper).map(payload);
    org.mockito.Mockito.verify(userService).createUser(mappedData);
    org.mockito.Mockito.verify(acknowledgment).acknowledge();
  }

  @Test
  void handleUserCreatedEventShouldThrowAndNotAckOnServiceErrorTest() {
    // Given
    UUID userId = UUID.randomUUID();
    UserCreatedEventDTO payload = new UserCreatedEventDTO().id(userId).username("john.doe");
    UserCreatedEventEnvelopeDTO event =
        new UserCreatedEventEnvelopeDTO().eventType(DomainEventType.USER_CREATED).payload(payload);
    UserCreationData mappedData = UserCreationData.builder().id(userId).name("john.doe").build();
    given(userEventMapper.map(payload)).willReturn(mappedData);
    doThrow(new RuntimeException("boom")).when(userService).createUser(mappedData);

    // When / Then
    assertThrows(
        RuntimeException.class,
        () -> consumer.handleUserCreatedEvent(event, "user-created", 0, 10L, acknowledgment));

    verifyNoInteractions(acknowledgment);
  }

  @Test
  void handleUserCreatedEventShouldRejectUnsupportedEventTypeTest() {
    // Given
    UserCreatedEventEnvelopeDTO event =
        new UserCreatedEventEnvelopeDTO()
            .eventType(DomainEventType.USER_UPDATED)
            .payload(new UserCreatedEventDTO().id(UUID.randomUUID()).username("john.doe"));

    // When / Then
    assertThrows(
        IllegalArgumentException.class,
        () -> consumer.handleUserCreatedEvent(event, "user-created", 0, 10L, acknowledgment));

    verifyNoInteractions(userEventMapper);
    verifyNoInteractions(userService);
    verifyNoInteractions(acknowledgment);
  }

  @Test
  void handleUserCreatedEventShouldRejectMissingPayloadTest() {
    // Given
    UserCreatedEventEnvelopeDTO event =
        new UserCreatedEventEnvelopeDTO().eventType(DomainEventType.USER_CREATED);

    // When / Then
    assertThrows(
        IllegalArgumentException.class,
        () -> consumer.handleUserCreatedEvent(event, "user-created", 0, 10L, acknowledgment));

    verifyNoInteractions(userEventMapper);
    verifyNoInteractions(userService);
    verifyNoInteractions(acknowledgment);
    verifyNoMoreInteractions(userEventMapper, userService, acknowledgment);
  }
}
