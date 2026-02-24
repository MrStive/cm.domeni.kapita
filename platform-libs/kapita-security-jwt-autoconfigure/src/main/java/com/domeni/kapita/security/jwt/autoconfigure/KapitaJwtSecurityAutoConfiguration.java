package com.domeni.kapita.security.jwt.autoconfigure;

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
  @ConditionalOnMissingBean(JwtDecoder.class)
  @ConditionalOnProperty(
      prefix = "kapita.security.jwt",
      name = {"issuer", "audience", "public-key-location"})
  public JwtDecoder jwtDecoder(KapitaJwtSecurityProperties properties) throws IOException {
    Assert.hasText(properties.getIssuer(), "kapita.security.jwt.issuer is required");
    Assert.hasText(properties.getAudience(), "kapita.security.jwt.audience is required");
    Assert.notNull(properties.getPublicKeyLocation(), "kapita.security.jwt.public-key-location is required");

    RSAPublicKey publicKey;
    try (InputStream inputStream = properties.getPublicKeyLocation().getInputStream()) {
      publicKey = RsaKeyConverters.x509().convert(inputStream);
    }

    NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(publicKey).build();
    OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(properties.getIssuer());
    OAuth2TokenValidator<Jwt> withAudience =
        new JwtClaimValidator<List<String>>(
            "aud", audiences -> audiences != null && audiences.contains(properties.getAudience()));
    jwtDecoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(withIssuer, withAudience));
    return jwtDecoder;
  }
}
