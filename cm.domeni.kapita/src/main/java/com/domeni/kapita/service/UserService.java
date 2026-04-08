package com.domeni.kapita.service;

import com.domeni.kapita.domain.exception.InvalidUserPayloadException;
import com.domeni.kapita.domain.user.UserCreationData;
import com.domeni.kapita.domain.user.UserFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserFactory userFactory;

  @Transactional
  public void createUser(UserCreationData data) {
    if (data == null) {
      throw new InvalidUserPayloadException("user creation payload is required");
    }
    if (data.id() == null) {
      throw new InvalidUserPayloadException("user creation payload is invalid");
    }

    String normalizedName = normalizeRequired(data.name());
    if (normalizedName == null) {
      throw new InvalidUserPayloadException("user creation payload is invalid");
    }

    userFactory.create(
        UserCreationData.builder()
            .id(data.id())
            .name(normalizedName)
            .firstname(normalizeOptional(data.firstname()))
            .lastname(normalizeOptional(data.lastname()))
            .email(normalizeOptional(data.email()))
            .build());
  }

  private String normalizeRequired(String value) {
    String normalizedValue = normalizeOptional(value);
    return normalizedValue == null || normalizedValue.isBlank() ? null : normalizedValue;
  }

  private String normalizeOptional(String value) {
    if (value == null) {
      return null;
    }
    String trimmedValue = value.trim();
    return trimmedValue.isEmpty() ? null : trimmedValue;
  }
}
