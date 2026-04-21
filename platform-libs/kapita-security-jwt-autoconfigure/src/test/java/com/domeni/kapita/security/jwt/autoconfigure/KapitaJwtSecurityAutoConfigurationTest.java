package com.domeni.kapita.security.jwt.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;

import com.domeni.kapita.security.jwt.CurrentUserProvider;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.security.oauth2.jwt.JwtDecoder;

class KapitaJwtSecurityAutoConfigurationTest {

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(KapitaJwtSecurityAutoConfiguration.class));

  @Test
  void createsJwtDecoderWhenConfigurationIsPresent() {
    contextRunner
        .withPropertyValues(
            "kapita.security.jwt.issuer=http://issuer.local",
            "kapita.security.jwt.audience=kapita-api",
            "kapita.security.jwt.public-key-location=classpath:security/test-jwt-public.pem")
        .run(
            context -> {
              assertThat(context).hasSingleBean(JwtDecoder.class);
              assertThat(context).hasSingleBean(CurrentUserProvider.class);
            });
  }

  @Test
  void createsJwtDecoderWhenJwkSetConfigurationIsPresent() {
    contextRunner
        .withPropertyValues(
            "kapita.security.jwt.issuer=http://issuer.local",
            "kapita.security.jwt.audience=kapita-api",
            "kapita.security.jwt.jwk-set-uri=http://issuer.local/protocol/openid-connect/certs")
        .run(
            context -> {
              assertThat(context).hasSingleBean(JwtDecoder.class);
              assertThat(context).hasSingleBean(CurrentUserProvider.class);
            });
  }

  @Test
  void doesNotCreateJwtDecoderWhenRequiredPropertiesAreMissing() {
    contextRunner
        .withPropertyValues("kapita.security.jwt.issuer=http://issuer.local")
        .run(
            context -> {
              assertThat(context).doesNotHaveBean(JwtDecoder.class);
              assertThat(context).hasSingleBean(CurrentUserProvider.class);
            });
  }
}
