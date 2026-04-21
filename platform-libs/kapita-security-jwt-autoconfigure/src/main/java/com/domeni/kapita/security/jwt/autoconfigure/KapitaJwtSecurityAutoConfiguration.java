package com.domeni.kapita.security.jwt.autoconfigure;

import com.domeni.kapita.security.jwt.CurrentUserProvider;
import java.io.IOException;
import java.io.InputStream;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.converter.RsaKeyConverters;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.util.Assert;

@AutoConfiguration
@ConditionalOnClass({JwtDecoder.class, NimbusJwtDecoder.class})
@EnableConfigurationProperties(KapitaJwtSecurityProperties.class)
public class KapitaJwtSecurityAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean(CurrentUserProvider.class)
  public CurrentUserProvider currentUserProvider() {
    return new SecurityContextCurrentUserProvider();
  }

  @Bean
  @ConditionalOnMissingBean(JwtDecoder.class)
  @ConditionalOnProperty(
      prefix = "kapita.security.jwt",
      name = {"issuer", "audience", "public-key-location"})
  public JwtDecoder jwtDecoderFromPublicKey(KapitaJwtSecurityProperties properties) throws IOException {
    Assert.hasText(properties.getIssuer(), "kapita.security.jwt.issuer is required");
    Assert.hasText(properties.getAudience(), "kapita.security.jwt.audience is required");
    Assert.notNull(properties.getPublicKeyLocation(), "kapita.security.jwt.public-key-location is required");

    RSAPublicKey publicKey;
    try (InputStream inputStream = properties.getPublicKeyLocation().getInputStream()) {
      publicKey = RsaKeyConverters.x509().convert(inputStream);
    }

    NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(publicKey).build();
    jwtDecoder.setJwtValidator(tokenValidator(properties));
    return jwtDecoder;
  }

  @Bean
  @ConditionalOnMissingBean(JwtDecoder.class)
  @ConditionalOnProperty(prefix = "kapita.security.jwt", name = {"issuer", "audience", "jwk-set-uri"})
  public JwtDecoder jwtDecoderFromJwkSetUri(KapitaJwtSecurityProperties properties) {
    Assert.hasText(properties.getIssuer(), "kapita.security.jwt.issuer is required");
    Assert.hasText(properties.getAudience(), "kapita.security.jwt.audience is required");
    Assert.hasText(properties.getJwkSetUri(), "kapita.security.jwt.jwk-set-uri is required");

    NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(properties.getJwkSetUri()).build();
    jwtDecoder.setJwtValidator(tokenValidator(properties));
    return jwtDecoder;
  }

  private OAuth2TokenValidator<Jwt> tokenValidator(KapitaJwtSecurityProperties properties) {
    OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(properties.getIssuer());
    OAuth2TokenValidator<Jwt> withAudience =
        new JwtClaimValidator<List<String>>(
            "aud", audiences -> audiences != null && audiences.contains(properties.getAudience()));
    return new DelegatingOAuth2TokenValidator<>(withIssuer, withAudience);
  }
}
