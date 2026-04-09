package com.domeni.kapita.repositories.impl;

import com.domeni.kapita.domain.user.User;
import com.domeni.kapita.domain.user.UserId;
import com.domeni.kapita.domain.user.UserRepository;
import com.domeni.kapita.repositories.UserSpringRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
  private final UserSpringRepository userSpringRepository;

  @Override
  public User save(User value) {
    return userSpringRepository.save(value);
  }

  @Override
  public Optional<User> findById(UserId id) {
    return userSpringRepository.findById(id);
  }
}
