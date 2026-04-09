package com.domeni.kapita.service.events.inbound;

import cm.domeni.generated.domeni.kapita.event.dto.DomainEventType;
import cm.domeni.generated.domeni.kapita.event.dto.UserCreatedEventDTO;
import com.domeni.kapita.kafka.inbound.InboundEventContext;
import com.domeni.kapita.kafka.inbound.InboundEventHandler;
import com.domeni.kapita.service.UserService;
import com.domeni.kapita.service.mapper.UserEventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCreatedInboundEventHandler implements InboundEventHandler<UserCreatedEventDTO> {

  private final UserService userService;
  private final UserEventMapper userEventMapper;

  @Override
  public void handle(UserCreatedEventDTO payload, InboundEventContext context) {
    userService.createUser(userEventMapper.map(payload));
  }

  @Override
  public Class<UserCreatedEventDTO> payloadType() {
    return UserCreatedEventDTO.class;
  }

  @Override
  public String eventType() {
    return DomainEventType.USER_CREATED.getValue();
  }
}
