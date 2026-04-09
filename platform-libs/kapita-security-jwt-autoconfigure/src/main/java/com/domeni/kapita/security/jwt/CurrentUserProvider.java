package com.domeni.kapita.security.jwt;

import java.util.Optional;
import java.util.UUID;

public interface CurrentUserProvider {

  Optional<UUID> getCurrentUserId();

  default UUID requireCurrentUserId() {
    return getCurrentUserId()
        .orElseThrow(() -> new IllegalStateException("current user id is unavailable"));
  }
}
