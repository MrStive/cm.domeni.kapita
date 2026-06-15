package com.domeni.kapita.api.error;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.domeni.kapita.api.DebtResource;
import com.domeni.kapita.domain.exception.DebtNotFoundException;
import com.domeni.kapita.domain.exception.InvalidDebtPayloadException;
import com.domeni.kapita.domain.user.UserId;
import com.domeni.kapita.security.jwt.CurrentUserProvider;
import com.domeni.kapita.service.DebtService;
import com.domeni.kapita.service.mapper.DebtMapper;
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

@WebMvcTest(controllers = DebtResource.class)
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
class DebtApiExceptionHandlerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private CurrentUserProvider currentUserProvider;
  @MockitoBean private DebtService debtService;
  @MockitoBean private DebtMapper debtMapper;

  @Test
  void createDebtWhenPayloadIsInvalidShouldReturnValidationPayloadTest() throws Exception {
    mockMvc
        .perform(post("/debt").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("KAPITA-400-VALIDATION"))
        .andExpect(jsonPath("$.path").value("/debt"))
        .andExpect(jsonPath("$.traceId").isNotEmpty())
        .andExpect(jsonPath("$.timestamp").exists());

    verifyNoInteractions(debtService);
  }

  @Test
  void createDebtWhenNestedAmountPayloadIsInvalidShouldReturnValidationPayloadTest()
      throws Exception {
    mockMvc
        .perform(
            post("/debt")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"type":"RECEIVABLE","counterpartyName":"Client A","amount":{"currency":"XAF"},"dueDate":"2026-04-11"}
                    """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("KAPITA-400-VALIDATION"))
        .andExpect(jsonPath("$.path").value("/debt"))
        .andExpect(jsonPath("$.traceId").isNotEmpty())
        .andExpect(jsonPath("$.timestamp").exists());

    verifyNoInteractions(debtService);
  }

  @Test
  void createDebtWhenServiceRejectsPayloadShouldReturnDomainPayloadTest() throws Exception {
    when(currentUserProvider.requireCurrentUserId()).thenReturn(UUID.randomUUID());
    when(debtService.createDebt(any(), any(UserId.class)))
        .thenThrow(new InvalidDebtPayloadException("debt creation is invalid"));

    mockMvc
        .perform(
            post("/debt")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"type":"RECEIVABLE","counterpartyName":"Client A","amount":{"value":5000,"currency":"XAF"},"dueDate":"2026-04-11"}
                    """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("KAPITA-400-005"))
        .andExpect(jsonPath("$.message").value("debt creation is invalid"))
        .andExpect(jsonPath("$.path").value("/debt"))
        .andExpect(jsonPath("$.traceId").isNotEmpty())
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @Test
  void fetchDebtsByTypeWhenServiceRejectsQueryShouldReturnDomainPayloadTest() throws Exception {
    when(currentUserProvider.requireCurrentUserId()).thenReturn(UUID.randomUUID());
    when(debtService.getDebtsByType(any(), any(), any(), any(UserId.class)))
        .thenThrow(new InvalidDebtPayloadException("debt query is invalid"));

    mockMvc
        .perform(
            get("/debt")
                .queryParam("type", "RECEIVABLE")
                .queryParam("pageNumber", "0")
                .queryParam("pageSize", "10"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("KAPITA-400-005"))
        .andExpect(jsonPath("$.message").value("debt query is invalid"))
        .andExpect(jsonPath("$.path").value("/debt"))
        .andExpect(jsonPath("$.traceId").isNotEmpty())
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @Test
  void fetchDebtsByTypeWhenServiceRejectsDefaultedQueryShouldReturnDomainPayloadTest()
      throws Exception {
    when(currentUserProvider.requireCurrentUserId()).thenReturn(UUID.randomUUID());
    when(debtService.getDebtsByType(any(), any(), any(), any(UserId.class)))
        .thenThrow(new InvalidDebtPayloadException("debt page size is invalid"));

    mockMvc
        .perform(get("/debt"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("KAPITA-400-005"))
        .andExpect(jsonPath("$.message").value("debt page size is invalid"))
        .andExpect(jsonPath("$.path").value("/debt"))
        .andExpect(jsonPath("$.traceId").isNotEmpty())
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @Test
  void fetchDebtsByTypeWhenPageNumberIsNegativeShouldReturnValidationPayloadTest()
      throws Exception {
    mockMvc
        .perform(
            get("/debt")
                .queryParam("type", "RECEIVABLE")
                .queryParam("pageNumber", "-1")
                .queryParam("pageSize", "10"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("KAPITA-400-VALIDATION"))
        .andExpect(jsonPath("$.path").value("/debt"))
        .andExpect(jsonPath("$.traceId").isNotEmpty())
        .andExpect(jsonPath("$.timestamp").exists());

    verifyNoInteractions(debtService);
  }

  @Test
  void markDebtAsPaidWhenDebtDoesNotExistShouldReturnNotFoundPayloadTest() throws Exception {
    UUID debtId = UUID.randomUUID();
    when(currentUserProvider.requireCurrentUserId()).thenReturn(UUID.randomUUID());
    when(debtService.markDebtAsPaid(any(UUID.class), any(UserId.class)))
        .thenThrow(new DebtNotFoundException());

    mockMvc
        .perform(put("/debt/{debtId}/paid", debtId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("KAPITA-404-002"))
        .andExpect(jsonPath("$.message").value("debt not found"))
        .andExpect(jsonPath("$.path").value("/debt/" + debtId + "/paid"))
        .andExpect(jsonPath("$.traceId").isNotEmpty())
        .andExpect(jsonPath("$.timestamp").exists());
  }
}
