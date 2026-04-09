package com.domeni.kapita.api.error;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.domeni.kapita.api.TransactionResource;
import com.domeni.kapita.domain.exception.InvalidTransactionPayloadException;
import com.domeni.kapita.domain.user.UserId;
import com.domeni.kapita.security.jwt.CurrentUserProvider;
import com.domeni.kapita.service.TransactionService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = TransactionResource.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ApiExceptionHandler.class)
@TestPropertySource(
    properties = {
      "spring.cloud.config.enabled=false",
      "spring.cloud.config.import-check.enabled=false",
      "spring.cloud.bus.enabled=false",
      "spring.cloud.stream.enabled=false",
      "kapita.security.jwt.public-key-location=classpath:security/jwt-public.pem",
      "kapita.security.jwt.issuer=http://auth-service.local",
      "kapita.security.jwt.audience=kapita-api",
    })
class TransactionApiExceptionHandlerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private CurrentUserProvider currentUserProvider;
  @MockitoBean private TransactionService transactionService;

  @Test
  void createTransactionWhenPayloadIsInvalidShouldReturnValidationPayloadTest() throws Exception {
    mockMvc
        .perform(post("/transaction").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("KAPITA-400-VALIDATION"))
        .andExpect(jsonPath("$.path").value("/transaction"))
        .andExpect(jsonPath("$.traceId").isNotEmpty())
        .andExpect(jsonPath("$.timestamp").exists());

    verifyNoInteractions(transactionService);
  }

  @Test
  void createTransactionWhenServiceRejectsPayloadShouldReturnDomainPayloadTest() throws Exception {
    when(currentUserProvider.requireCurrentUserId()).thenReturn(UUID.randomUUID());
    when(transactionService.createTransaction(any(), any(UserId.class)))
        .thenThrow(
            new InvalidTransactionPayloadException("transaction category is invalid for type"));

    mockMvc
        .perform(
            post("/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"type\":\"INCOMING\",\"category\":\"STOCK\",\"amount\":1000}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("KAPITA-400-004"))
        .andExpect(jsonPath("$.message").value("transaction category is invalid for type"))
        .andExpect(jsonPath("$.path").value("/transaction"))
        .andExpect(jsonPath("$.traceId").isNotEmpty())
        .andExpect(jsonPath("$.timestamp").exists());
  }
}
