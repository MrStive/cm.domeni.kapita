package com.domeni.kapita.service.events.inbound;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import cm.domeni.generated.domeni.kapita.event.dto.UserCreatedEventDTO;
import com.domeni.kapita.domain.user.UserCreationData;
import com.domeni.kapita.kafka.inbound.InboundEventContext;
import com.domeni.kapita.service.UserService;
import com.domeni.kapita.service.mapper.UserEventMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserCreatedInboundEventHandlerTest {

  @Mock private UserService userService;
  @Mock private UserEventMapper userEventMapper;

  @InjectMocks private UserCreatedInboundEventHandler userCreatedInboundEventHandler;

  @Test
  void handleShouldCallCreateUserUseCaseTest() {
    UUID userId = UUID.randomUUID();
    UserCreatedEventDTO event = new UserCreatedEventDTO().id(userId).username("john.doe");
    UserCreationData command = UserCreationData.builder().id(userId).name("john.doe").build();
    given(userEventMapper.map(event)).willReturn(command);

    userCreatedInboundEventHandler.handle(
        event, new InboundEventContext("authentis.user.created", 0, 10L));

    then(userEventMapper).should().map(event);
    then(userService).should().createUser(command);
  }
}
