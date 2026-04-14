package com.domeni.kapita.api;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import cm.domeni.generated.domeni.kapita.dto.CreateDebtDTO;
import cm.domeni.generated.domeni.kapita.dto.CreationResponseDTO;
import cm.domeni.generated.domeni.kapita.dto.DebtDTO;
import cm.domeni.generated.domeni.kapita.dto.DebtPageDTO;
import cm.domeni.generated.domeni.kapita.dto.DebtStatusDTO;
import cm.domeni.generated.domeni.kapita.dto.DebtTypeDTO;
import cm.domeni.generated.domeni.kapita.dto.MoneyDTO;
import com.domeni.kapita.domain.user.UserId;
import com.domeni.kapita.security.jwt.CurrentUserProvider;
import com.domeni.kapita.service.DebtService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

  @Test
  void fetchDebtsByTypeShouldReturnDebtPageForCurrentUserTest() {
    UUID currentUserId = UUID.randomUUID();
    DebtPageDTO expectedResponse =
        new DebtPageDTO()
            .items(
                List.of(
                    new DebtDTO()
                        .id(UUID.randomUUID())
                        .type(DebtTypeDTO.RECEIVABLE)
                        .counterpartyName("Client A")
                        .amount(new MoneyDTO().currency("XAF").value(new BigDecimal("5000.00")))
                        .dueDate(LocalDate.of(2026, 4, 11))
                        .status(DebtStatusDTO.UNPAID)
                        .createdAt(LocalDateTime.of(2026, 4, 11, 8, 0))))
            .pageNumber(0)
            .pageSize(10)
            .totalElements(1L)
            .totalPages(1);

    when(currentUserProvider.requireCurrentUserId()).thenReturn(currentUserId);
    when(debtService.getDebtsByType(
            com.domeni.kapita.domain.debt.DebtType.RECEIVABLE, 0, 10, new UserId(currentUserId)))
        .thenReturn(expectedResponse);

    // spotless:off
        DebtPageDTO response =
                given()
                        .standaloneSetup(new DebtResource(currentUserProvider, debtService))
                        .queryParam("type", "RECEIVABLE")
                        .queryParam("pageNumber", 0)
                        .queryParam("pageSize", 10)
                .when()
                        .get("/debt")
                .then()
                        .statusCode(200)
                        .extract().body().as(DebtPageDTO.class);
        // spotless:on
    assertThat(response.getPageNumber()).isEqualTo(0);
    assertThat(response.getPageSize()).isEqualTo(10);
    assertThat(response.getTotalElements()).isEqualTo(1L);
    assertThat(response.getItems()).hasSize(1);
    assertThat(response.getItems().getFirst().getCounterpartyName()).isEqualTo("Client A");
  }

  @Test
  void fetchDebtsByTypeShouldUseDefaultPaginationAndAllTypesWhenQueryIsMissingTest() {
    UUID currentUserId = UUID.randomUUID();
    DebtPageDTO expectedResponse =
        new DebtPageDTO()
            .items(List.of())
            .pageNumber(0)
            .pageSize(10)
            .totalElements(0L)
            .totalPages(0);

    when(currentUserProvider.requireCurrentUserId()).thenReturn(currentUserId);
    when(debtService.getDebtsByType(null, 0, 10, new UserId(currentUserId)))
        .thenReturn(expectedResponse);

    // spotless:off
        DebtPageDTO response =
                given()
                        .standaloneSetup(new DebtResource(currentUserProvider, debtService))
                .when()
                        .get("/debt")
                .then()
                        .statusCode(200)
                        .extract().body().as(DebtPageDTO.class);
        // spotless:on
    assertThat(response.getPageNumber()).isEqualTo(0);
    assertThat(response.getPageSize()).isEqualTo(10);
    assertThat(response.getItems()).isEmpty();
  }

  @Test
  void createDebtShouldReturnCreatedDebtIdTest() {
    UUID debtId = UUID.randomUUID();
    UUID currentUserId = UUID.randomUUID();
    CreateDebtDTO input =
        new CreateDebtDTO()
            .type(DebtTypeDTO.RECEIVABLE)
            .counterpartyName("Client A")
            .amount(new MoneyDTO().currency("XAF").value(new BigDecimal("5000.00")))
            .dueDate(LocalDate.of(2026, 4, 11));

    when(currentUserProvider.requireCurrentUserId()).thenReturn(currentUserId);
    when(debtService.createDebt(any(CreateDebtDTO.class), any(UserId.class))).thenReturn(debtId);

    // spotless:off
        CreationResponseDTO response =
                given()
                        .standaloneSetup(new DebtResource(currentUserProvider, debtService))
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
    UUID currentUserId = UUID.randomUUID();
    DebtDTO expectedResponse =
        new DebtDTO()
            .id(debtId)
            .type(DebtTypeDTO.PAYABLE)
            .counterpartyName("Fournisseur B")
            .amount(new MoneyDTO().currency("XAF").value(new BigDecimal("15000.00")))
            .dueDate(LocalDate.of(2026, 4, 12))
            .status(DebtStatusDTO.PAID)
            .createdAt(LocalDateTime.of(2026, 4, 10, 9, 30));

    when(currentUserProvider.requireCurrentUserId()).thenReturn(currentUserId);
    when(debtService.markDebtAsPaid(debtId, new UserId(currentUserId)))
        .thenReturn(expectedResponse);

    // spotless:off
        DebtDTO response =
                given()
                        .standaloneSetup(new DebtResource(currentUserProvider, debtService))
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
