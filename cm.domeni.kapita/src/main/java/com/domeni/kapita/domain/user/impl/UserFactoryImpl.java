package com.domeni.kapita.domain.user.impl;

import com.domeni.kapita.domain.user.User;
import com.domeni.kapita.domain.user.UserCreationData;
import com.domeni.kapita.domain.user.UserEmail;
import com.domeni.kapita.domain.user.UserFactory;
import com.domeni.kapita.domain.user.UserFirstName;
import com.domeni.kapita.domain.user.UserId;
import com.domeni.kapita.domain.user.UserLastName;
import com.domeni.kapita.domain.user.UserName;
import com.domeni.kapita.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class UserFactoryImpl implements UserFactory {
  private final UserRepository userRepository;

  @Override
  public User create(UserCreationData userCreationData) {
    UserId userId = new UserId(userCreationData.id());
    return userRepository
        .findById(userId)
        .map(
            existingUser -> {
              log.info("User with id={} already exists, skipping creation", userId.getValue());
              return existingUser;
            })
        .orElseGet(
            () ->
                userRepository.save(
                    User.builder()
                        .id(userId)
                        .name(new UserName(userCreationData.name()))
                        .firstname(mapFirstName(userCreationData.firstname()))
                        .lastname(mapLastName(userCreationData.lastname()))
                        .email(mapEmail(userCreationData.email()))
                        .build()));
  }

  private UserFirstName mapFirstName(String value) {
    return value == null ? null : new UserFirstName(value);
  }

  private UserLastName mapLastName(String value) {
    return value == null ? null : new UserLastName(value);
  }

  private UserEmail mapEmail(String value) {
    return value == null ? null : new UserEmail(value);
  }
}
