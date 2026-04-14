package com.domeni.kapita.api.error;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.domeni.kapita.api.TransactionResource;
import com.domeni.kapita.domain.exception.InvalidTransactionPayloadException;
import com.domeni.kapita.domain.user.UserId;
import com.domeni.kapita.security.jwt.CurrentUserProvider;
import com.domeni.kapita.service.TransactionService;
import java.time.LocalDate;
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

  @Test
  void fetchTransactionBalanceWhenServiceRejectsPeriodShouldReturnDomainPayloadTest()
      throws Exception {
    when(currentUserProvider.requireCurrentUserId()).thenReturn(UUID.randomUUID());
    when(transactionService.getBalance(
            any(LocalDate.class), any(LocalDate.class), any(UserId.class)))
        .thenThrow(new InvalidTransactionPayloadException("transaction period is invalid"));

    mockMvc
        .perform(
            get("/transaction/balance")
                .queryParam("startDate", "2026-02-10")
                .queryParam("endDate", "2026-02-01"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("KAPITA-400-004"))
        .andExpect(jsonPath("$.message").value("transaction period is invalid"))
        .andExpect(jsonPath("$.path").value("/transaction/balance"))
        .andExpect(jsonPath("$.traceId").isNotEmpty())
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @Test
  void fetchTransactionAmountByTypeWhenServiceRejectsPeriodShouldReturnDomainPayloadTest()
      throws Exception {
    when(currentUserProvider.requireCurrentUserId()).thenReturn(UUID.randomUUID());
    when(transactionService.getAmountByType(
            any(LocalDate.class),
            any(LocalDate.class),
            any(com.domeni.kapita.domain.transaction.TransactionType.class),
            any(UserId.class)))
        .thenThrow(new InvalidTransactionPayloadException("transaction period is invalid"));

    mockMvc
        .perform(
            get("/transaction/amount")
                .queryParam("startDate", "2026-02-10")
                .queryParam("endDate", "2026-02-01")
                .queryParam("type", "EXPENSE"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("KAPITA-400-004"))
        .andExpect(jsonPath("$.message").value("transaction period is invalid"))
        .andExpect(jsonPath("$.path").value("/transaction/amount"))
        .andExpect(jsonPath("$.traceId").isNotEmpty())
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @Test
  void fetchTransactionsWhenServiceRejectsQueryShouldReturnDomainPayloadTest() throws Exception {
    when(currentUserProvider.requireCurrentUserId()).thenReturn(UUID.randomUUID());
    when(transactionService.getTransactions(any(), any(), any(), any(UserId.class)))
        .thenThrow(new InvalidTransactionPayloadException("transaction page size is invalid"));

    mockMvc
        .perform(get("/transaction").queryParam("pageSize", "10"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("KAPITA-400-004"))
        .andExpect(jsonPath("$.message").value("transaction page size is invalid"))
        .andExpect(jsonPath("$.path").value("/transaction"))
        .andExpect(jsonPath("$.traceId").isNotEmpty())
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @Test
  void fetchTransactionsWhenPageNumberIsNegativeShouldReturnValidationPayloadTest()
      throws Exception {
    mockMvc
        .perform(get("/transaction").queryParam("pageNumber", "-1"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("KAPITA-400-VALIDATION"))
        .andExpect(jsonPath("$.path").value("/transaction"))
        .andExpect(jsonPath("$.traceId").isNotEmpty())
        .andExpect(jsonPath("$.timestamp").exists());

    verifyNoInteractions(transactionService);
  }
}
