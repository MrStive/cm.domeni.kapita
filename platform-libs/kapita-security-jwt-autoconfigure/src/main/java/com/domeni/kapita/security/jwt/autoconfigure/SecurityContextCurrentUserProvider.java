package com.domeni.kapita.security.jwt.autoconfigure;

import com.domeni.kapita.security.jwt.CurrentUserProvider;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

final class SecurityContextCurrentUserProvider implements CurrentUserProvider {

  @Override
  public Optional<UUID> getCurrentUserId() {
    return Optional.ofNullable(SecurityContextHolder.getContext())
        .map(SecurityContext::getAuthentication)
        .filter(JwtAuthenticationToken.class::isInstance)
        .map(JwtAuthenticationToken.class::cast)
        .map(JwtAuthenticationToken::getTokenAttributes)
        .map(tokenAttributes -> tokenAttributes.get(JwtClaimNames.SUB))
        .map(String.class::cast)
        .filter(subject -> !subject.isBlank())
        .map(this::parseUuid);
  }

  private UUID parseUuid(String subject) {
    try {
      return UUID.fromString(subject);
    } catch (IllegalArgumentException exception) {
      throw new IllegalStateException("current user id claim 'sub' is not a valid UUID", exception);
    }
  }
}
