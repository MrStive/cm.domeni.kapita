package com.domeni.kapita.api;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import cm.domeni.generated.domeni.kapita.dto.CreateDebtDTO;
import cm.domeni.generated.domeni.kapita.dto.CreationResponseDTO;
import cm.domeni.generated.domeni.kapita.dto.DebtDTO;
import cm.domeni.generated.domeni.kapita.dto.DebtPageDTO;
import cm.domeni.generated.domeni.kapita.dto.DebtStatusDTO;
import cm.domeni.generated.domeni.kapita.dto.DebtTypeDTO;
import cm.domeni.generated.domeni.kapita.dto.MoneyDTO;
import com.domeni.kapita.domain.debt.Debt;
import com.domeni.kapita.domain.debt.DebtData;
import com.domeni.kapita.domain.debt.DebtPage;
import com.domeni.kapita.domain.debt.DebtType;
import com.domeni.kapita.domain.user.UserId;
import com.domeni.kapita.security.jwt.CurrentUserProvider;
import com.domeni.kapita.service.DebtService;
import com.domeni.kapita.service.mapper.DebtMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

@ExtendWith(MockitoExtension.class)
class DebtResourceTest {
  @Mock private CurrentUserProvider currentUserProvider;
  @Mock private DebtService debtService;
  @Mock private DebtMapper debtMapper;

  @Test
  void fetchDebtsByTypeShouldReturnDebtPageForCurrentUserTest() {
    UUID currentUserIdValue = UUID.randomUUID();
    UserId currentUserId = new UserId(currentUserIdValue);

    DebtPage domainPage = mock(DebtPage.class);
    DebtPageDTO expectedResponse =
        new DebtPageDTO()
            .items(List.of(new DebtDTO().counterpartyName("Client A")))
            .pageNumber(0)
            .pageSize(10)
            .totalElements(1L)
            .totalPages(1);

    when(currentUserProvider.requireCurrentUserId()).thenReturn(currentUserIdValue);
    when(debtMapper.map(DebtTypeDTO.RECEIVABLE)).thenReturn(DebtType.RECEIVABLE);
    when(debtService.getDebtsByType(DebtType.RECEIVABLE, 0, 10, currentUserId))
        .thenReturn(domainPage);
    when(debtMapper.map(domainPage)).thenReturn(expectedResponse);

    // spotless:off
        DebtPageDTO response =
                given()
                        .standaloneSetup(new DebtResource(currentUserProvider, debtService, debtMapper))
                        .queryParam("type", "RECEIVABLE")
                        .queryParam("pageNumber", 0)
                        .queryParam("pageSize", 10)
                .when()
                        .get("/debt")
                .then()
                        .statusCode(200)
                        .extract().body().as(DebtPageDTO.class);
        // spotless:on
    assertThat(response.getItems().getFirst().getCounterpartyName()).isEqualTo("Client A");
  }

  @Test
  void createDebtShouldReturnCreatedDebtIdTest() {
    UUID debtId = UUID.randomUUID();
    UUID currentUserIdValue = UUID.randomUUID();
    UserId currentUserId = new UserId(currentUserIdValue);
    CreateDebtDTO input =
        new CreateDebtDTO()
            .type(DebtTypeDTO.RECEIVABLE)
            .counterpartyName("Client A")
            .amount(new MoneyDTO().currency("XAF").value(new BigDecimal("5000")))
            .dueDate(LocalDate.now());
    DebtData domainData = mock(DebtData.class);

    when(currentUserProvider.requireCurrentUserId()).thenReturn(currentUserIdValue);
    when(debtMapper.map(any(CreateDebtDTO.class))).thenReturn(domainData);
    when(debtService.createDebt(eq(domainData), eq(currentUserId))).thenReturn(debtId);

    // spotless:off
        CreationResponseDTO response =
                given()
                        .standaloneSetup(new DebtResource(currentUserProvider, debtService, debtMapper))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .body(input)
                .when()
                        .post("/debt")
                .then()
                        .statusCode(201)
                        .extract().body().as(CreationResponseDTO.class);
        // spotless:on
    assertThat(response.getNewId()).isEqualTo(debtId);
  }

  @Test
  void markDebtAsPaidShouldReturnUpdatedDebtTest() {
    UUID debtId = UUID.randomUUID();
    UUID currentUserIdValue = UUID.randomUUID();
    UserId currentUserId = new UserId(currentUserIdValue);

    Debt domainDebt = mock(Debt.class);
    DebtDTO expectedResponse = new DebtDTO().id(debtId).status(DebtStatusDTO.PAID);

    when(currentUserProvider.requireCurrentUserId()).thenReturn(currentUserIdValue);
    when(debtService.markDebtAsPaid(eq(debtId), eq(currentUserId))).thenReturn(domainDebt);
    when(debtMapper.map(domainDebt)).thenReturn(expectedResponse);

    // spotless:off
        DebtDTO response =
                given()
                        .standaloneSetup(new DebtResource(currentUserProvider, debtService, debtMapper))
                        .pathParam("debtId", debtId)
                .when()
                        .put("/debt/{debtId}/paid")
                .then()
                        .statusCode(200)
                        .extract().body().as(DebtDTO.class);
        // spotless:on
    assertThat(response.getId()).isEqualTo(debtId);
    assertThat(response.getStatus()).isEqualTo(DebtStatusDTO.PAID);
  }
}
