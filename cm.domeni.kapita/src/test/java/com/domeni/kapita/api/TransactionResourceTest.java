package com.domeni.kapita.api;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import cm.domeni.generated.domeni.kapita.dto.CreateTransactionDTO;
import cm.domeni.generated.domeni.kapita.dto.CreationResponseDTO;
import cm.domeni.generated.domeni.kapita.dto.MoneyDTO;
import cm.domeni.generated.domeni.kapita.dto.TransactionCategoryDTO;
import cm.domeni.generated.domeni.kapita.dto.TransactionDTO;
import cm.domeni.generated.domeni.kapita.dto.TransactionPageDTO;
import cm.domeni.generated.domeni.kapita.dto.TransactionTypeDTO;
import com.domeni.kapita.domain.user.UserId;
import com.domeni.kapita.security.jwt.CurrentUserProvider;
import com.domeni.kapita.service.TransactionService;
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
class TransactionResourceTest {
  @Mock private CurrentUserProvider currentUserProvider;
  @Mock private TransactionService transactionService;

  @Test
  void createTransactionShouldReturnCreatedTransactionIdTest() {
    UUID transactionId = UUID.randomUUID();
    UUID currentUserId = UUID.randomUUID();
    CreateTransactionDTO input =
        new CreateTransactionDTO()
            .type(TransactionTypeDTO.INCOMING)
            .category(TransactionCategoryDTO.SALE)
            .amount(new BigDecimal("12500.00"))
            .description("shop sale");

    when(currentUserProvider.requireCurrentUserId()).thenReturn(currentUserId);
    when(transactionService.createTransaction(any(CreateTransactionDTO.class), any(UserId.class)))
        .thenReturn(transactionId);

    // spotless:off
        CreationResponseDTO response =
                given()
                        .standaloneSetup(new TransactionResource(currentUserProvider, transactionService))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .body(input)
                .when()
                        .post("/transaction")
                .then()
                        .statusCode(201)
                        .extract().body().as(CreationResponseDTO.class);
        // spotless:on
    assertThat(response.getNewId()).isEqualTo(transactionId);
  }

  @Test
  void fetchTransactionsShouldReturnTransactionPageForCurrentUserTest() {
    UUID currentUserId = UUID.randomUUID();
    TransactionPageDTO expectedResponse =
        new TransactionPageDTO()
            .items(
                List.of(
                    new TransactionDTO()
                        .id(UUID.randomUUID())
                        .type(TransactionTypeDTO.EXPENSE)
                        .category(TransactionCategoryDTO.TRANSPORT)
                        .amount(new BigDecimal("2500.00"))
                        .description("taxi")
                        .createdAt(LocalDateTime.of(2026, 4, 13, 9, 45))))
            .pageNumber(0)
            .pageSize(10)
            .totalElements(1L)
            .totalPages(1);

    when(currentUserProvider.requireCurrentUserId()).thenReturn(currentUserId);
    when(transactionService.getTransactions(any(), any(), any(), any(UserId.class)))
        .thenReturn(expectedResponse);

    // spotless:off
        TransactionPageDTO response =
                given()
                        .standaloneSetup(new TransactionResource(currentUserProvider, transactionService))
                .when()
                        .get("/transaction")
                .then()
                        .statusCode(200)
                        .extract().body().as(TransactionPageDTO.class);
        // spotless:on
    assertThat(response.getPageNumber()).isEqualTo(0);
    assertThat(response.getPageSize()).isEqualTo(10);
    assertThat(response.getItems()).hasSize(1);
    assertThat(response.getItems().getFirst().getDescription()).isEqualTo("taxi");
  }

  @Test
  void fetchTransactionBalanceShouldReturnBalanceForCurrentUserAndPeriodTest() {
    UUID currentUserId = UUID.randomUUID();
    LocalDate startDate = LocalDate.of(2026, 1, 1);
    LocalDate endDate = LocalDate.of(2026, 1, 31);
    MoneyDTO expectedResponse = new MoneyDTO().currency("XAF").value(new BigDecimal("21249.50"));

    when(currentUserProvider.requireCurrentUserId()).thenReturn(currentUserId);
    when(transactionService.getBalance(startDate, endDate, new UserId(currentUserId)))
        .thenReturn(expectedResponse);

    // spotless:off
        MoneyDTO response =
                given()
                        .standaloneSetup(new TransactionResource(currentUserProvider, transactionService))
                        .queryParam("startDate", startDate.toString())
                        .queryParam("endDate", endDate.toString())
                .when()
                        .get("/transaction/balance")
                .then()
                        .statusCode(200)
                        .extract().body().as(MoneyDTO.class);
        // spotless:on
    assertThat(response.getCurrency()).isEqualTo("XAF");
    assertThat(response.getValue()).isEqualByComparingTo("21249.50");
  }

  @Test
  void fetchTransactionAmountByTypeShouldReturnAmountForCurrentUserAndPeriodTest() {
    UUID currentUserId = UUID.randomUUID();
    LocalDate startDate = LocalDate.of(2026, 2, 1);
    LocalDate endDate = LocalDate.of(2026, 2, 28);
    MoneyDTO expectedResponse = new MoneyDTO().currency("XAF").value(new BigDecimal("4250.75"));

    when(currentUserProvider.requireCurrentUserId()).thenReturn(currentUserId);
    when(transactionService.getAmountByType(
            startDate,
            endDate,
            com.domeni.kapita.domain.transaction.TransactionType.EXPENSE,
            new UserId(currentUserId)))
        .thenReturn(expectedResponse);

    // spotless:off
        MoneyDTO response =
                given()
                        .standaloneSetup(new TransactionResource(currentUserProvider, transactionService))
                        .queryParam("startDate", startDate.toString())
                        .queryParam("endDate", endDate.toString())
                        .queryParam("type", "EXPENSE")
                .when()
                        .get("/transaction/amount")
                .then()
                        .statusCode(200)
                        .extract().body().as(MoneyDTO.class);
        // spotless:on
    assertThat(response.getCurrency()).isEqualTo("XAF");
    assertThat(response.getValue()).isEqualByComparingTo("4250.75");
  }
}
