package com.domeni.kapita.security.jwt.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.domeni.kapita.security.jwt.CurrentUserProvider;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

class SecurityContextCurrentUserProviderTest {

  private final CurrentUserProvider currentUserProvider = new SecurityContextCurrentUserProvider();

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void requireCurrentUserIdShouldExtractUuidFromJwtSubjectTest() {
    UUID expectedUserId = UUID.randomUUID();
    SecurityContextHolder.getContext().setAuthentication(jwtAuthentication(expectedUserId.toString()));

    assertThat(currentUserProvider.requireCurrentUserId()).isEqualTo(expectedUserId);
  }

  @Test
  void getCurrentUserIdWithoutAuthenticationShouldReturnEmptyTest() {
    assertThat(currentUserProvider.getCurrentUserId()).isEmpty();
  }

  @Test
  void requireCurrentUserIdWithInvalidSubjectShouldThrowIllegalStateExceptionTest() {
    SecurityContextHolder.getContext().setAuthentication(jwtAuthentication("not-a-uuid"));

    assertThatThrownBy(currentUserProvider::requireCurrentUserId)
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("current user id claim 'sub' is not a valid UUID");
  }

  private JwtAuthenticationToken jwtAuthentication(String subject) {
    Jwt jwt =
        Jwt.withTokenValue("token")
            .header("alg", "none")
            .claim(JwtClaimNames.SUB, subject)
            .build();
    return new JwtAuthenticationToken(jwt);
  }
}
