package com.domeni.kapita.repositories.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import com.domeni.kapita.domain.user.User;
import com.domeni.kapita.repositories.UserSpringRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserRepositoryImplTest {

  @InjectMocks private UserRepositoryImpl objectUnderTest;

  @Mock private UserSpringRepository userSpringRepository;

  @Test
  void saveShouldDelegateToSpringRepositoryTest() {
    // Given
    User user = mock(User.class);
    User persistedUser = mock(User.class);
    given(userSpringRepository.save(user)).willReturn(persistedUser);

    // When
    User result = objectUnderTest.save(user);

    // Then
    assertThat(result).isSameAs(persistedUser);
    then(userSpringRepository).should().save(user);
  }
}
