package com.domeni.kapita.config;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.domeni.kapita.api.DemoResource;
import com.domeni.kapita.api.TransactionResource;
import com.domeni.kapita.security.jwt.autoconfigure.KapitaJwtSecurityAutoConfiguration;
import com.domeni.kapita.service.DemoService;
import com.domeni.kapita.service.TransactionService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {DemoResource.class, TransactionResource.class})
@Import(SecurityConfig.class)
@ImportAutoConfiguration(KapitaJwtSecurityAutoConfiguration.class)
@TestPropertySource(
    properties = {
      "kapita.security.jwt.public-key-location=classpath:security/jwt-public.pem",
      "kapita.security.jwt.issuer=http://auth-service.local",
      "kapita.security.jwt.audience=kapita-api",
      "spring.cloud.config.enabled=false",
      "spring.cloud.config.import-check.enabled=false",
      "spring.cloud.bus.enabled=false",
      "spring.cloud.stream.enabled=false",
      "kapita.security.expose-docs=true",
    })
class SecurityConfigTest {

  private static final RSAPrivateKey PRIVATE_KEY = loadPrivateKey();
  private static final String SECURITY_TEST_USER_ID = "84bf7fde-8943-48ce-a420-4d4c85467f44";

  @Autowired private MockMvc mockMvc;

  @MockitoBean private DemoService demoService;

  @MockitoBean private TransactionService transactionService;

  @BeforeEach
  void setUp() {
    when(demoService.fetchAllDemos()).thenReturn(List.of());
  }

  @Test
  void fetchAllDemoWhenAudienceIsInvalidShouldReturnUnauthorizedTest() throws Exception {
    String token = createToken(List.of("demo:read:all"), List.of("unexpected-audience"));

    mockMvc
        .perform(get("/demo").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
        .andExpect(status().isUnauthorized());

    verifyNoInteractions(demoService);
  }

  @Test
  void fetchAllDemoWhenScopeIsMissingShouldReturnForbiddenTest() throws Exception {
    String token = createToken(List.of("demo:create"), List.of("kapita-api"));

    mockMvc
        .perform(get("/demo").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
        .andExpect(status().isForbidden());

    verifyNoInteractions(demoService);
  }

  @Test
  void optionsRequestShouldBeAllowedWithoutAuthenticationTest() throws Exception {
    mockMvc
        .perform(
            options("/demo")
                .header("Origin", "http://localhost")
                .header("Access-Control-Request-Method", "GET"))
        .andExpect(status().isOk());

    verifyNoInteractions(demoService);
  }

  @Test
  void actuatorHealthShouldNotBeRejectedBySecurityTest() throws Exception {
    mockMvc.perform(get("/actuator/health")).andExpect(status().isNotFound());

    verifyNoInteractions(demoService);
  }

  @Test
  void apiDocsShouldNotBeRejectedWhenDocsExposureIsEnabledTest() throws Exception {
    mockMvc.perform(get("/v3/api-docs")).andExpect(status().isNotFound());

    verifyNoInteractions(demoService);
  }

  @Test
  void createTransactionWhenScopeIsMissingShouldReturnForbiddenTest() throws Exception {
    String token = createToken(List.of("demo:create"), List.of("kapita-api"));

    mockMvc
        .perform(
            post("/transaction")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"type\":\"INCOMING\",\"category\":\"SALE\",\"amount\":1000}"))
        .andExpect(status().isForbidden());

    verifyNoInteractions(transactionService);
  }

  @Test
  void fetchTransactionBalanceWhenScopeIsMissingShouldReturnForbiddenTest() throws Exception {
    String token = createToken(List.of("transaction:create"), List.of("kapita-api"));

    mockMvc
        .perform(
            get("/transaction/balance")
                .queryParam("startDate", "2026-01-01")
                .queryParam("endDate", "2026-01-31")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
        .andExpect(status().isForbidden());

    verifyNoInteractions(transactionService);
  }

  private String createToken(List<String> scopes, List<String> audiences) {
    Instant now = Instant.now();
    String scopeValue = String.join(" ", scopes);

    JWTClaimsSet claimsSet =
        new JWTClaimsSet.Builder()
            .issuer("http://auth-service.local")
            .subject(SECURITY_TEST_USER_ID)
            .issueTime(Date.from(now))
            .expirationTime(Date.from(now.plusSeconds(3600)))
            .jwtID(UUID.randomUUID().toString())
            .audience(audiences)
            .claim("scope", scopeValue)
            .build();

    SignedJWT signedJwt =
        new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).build(), claimsSet);

    try {
      signedJwt.sign(new RSASSASigner(PRIVATE_KEY));
      return signedJwt.serialize();
    } catch (JOSEException e) {
      throw new IllegalStateException("Unable to sign security test jwt token", e);
    }
  }

  private static RSAPrivateKey loadPrivateKey() {
    try {
      String pem =
          new String(
                  new ClassPathResource("security/jwt-private.pem").getInputStream().readAllBytes(),
                  StandardCharsets.UTF_8)
              .replace("-----BEGIN PRIVATE KEY-----", "")
              .replace("-----END PRIVATE KEY-----", "")
              .replaceAll("\\s", "");

      byte[] decodedPem = Base64.getDecoder().decode(pem);
      PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedPem);
      return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(keySpec);
    } catch (Exception e) {
      throw new IllegalStateException("Unable to load RSA private key for security tests", e);
    }
  }
}
