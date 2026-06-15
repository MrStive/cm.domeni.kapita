package com.domeni.kapita.api;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import cm.domeni.generated.domeni.kapita.dto.CreateTransactionDTO;
import cm.domeni.generated.domeni.kapita.dto.CreationResponseDTO;
import cm.domeni.generated.domeni.kapita.dto.MoneyDTO;
import cm.domeni.generated.domeni.kapita.dto.TransactionCategoryDTO;
import cm.domeni.generated.domeni.kapita.dto.TransactionDTO;
import cm.domeni.generated.domeni.kapita.dto.TransactionPageDTO;
import cm.domeni.generated.domeni.kapita.dto.TransactionTypeDTO;
import com.domeni.kapita.domain.transaction.TransactionData;
import com.domeni.kapita.domain.transaction.TransactionPage;
import com.domeni.kapita.domain.transaction.TransactionType;
import com.domeni.kapita.domain.user.UserId;
import com.domeni.kapita.security.jwt.CurrentUserProvider;
import com.domeni.kapita.service.TransactionService;
import com.domeni.kapita.service.mapper.TransactionMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import javax.money.MonetaryAmount;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

@ExtendWith(MockitoExtension.class)
class TransactionResourceTest {
  @Mock private CurrentUserProvider currentUserProvider;
  @Mock private TransactionService transactionService;
  @Mock private TransactionMapper transactionMapper;

  @Test
  void createTransactionShouldReturnCreatedTransactionIdTest() {
    UUID transactionId = UUID.randomUUID();
    UUID currentUserIdValue = UUID.randomUUID();
    UserId currentUserId = new UserId(currentUserIdValue);
    CreateTransactionDTO input =
        new CreateTransactionDTO()
            .type(TransactionTypeDTO.INCOMING)
            .category(TransactionCategoryDTO.SALE)
            .amount(new BigDecimal("100"));
    TransactionData domainData = mock(TransactionData.class);

    when(currentUserProvider.requireCurrentUserId()).thenReturn(currentUserIdValue);
    when(transactionMapper.map(any(CreateTransactionDTO.class))).thenReturn(domainData);
    when(transactionService.createTransaction(eq(domainData), eq(currentUserId)))
        .thenReturn(transactionId);

    // spotless:off
        CreationResponseDTO response =
                given()
                        .standaloneSetup(new TransactionResource(currentUserProvider, transactionService, transactionMapper))
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
    UUID currentUserIdValue = UUID.randomUUID();
    UserId currentUserId = new UserId(currentUserIdValue);
    TransactionPage domainPage = mock(TransactionPage.class);
    TransactionPageDTO expectedResponse =
        new TransactionPageDTO()
            .items(List.of(new TransactionDTO().description("taxi")))
            .pageNumber(0)
            .pageSize(10)
            .totalElements(1L)
            .totalPages(1);

    when(currentUserProvider.requireCurrentUserId()).thenReturn(currentUserIdValue);
    when(transactionMapper.map(TransactionTypeDTO.EXPENSE)).thenReturn(TransactionType.EXPENSE);
    when(transactionService.getTransactions(
            eq(TransactionType.EXPENSE), any(), any(), eq(currentUserId)))
        .thenReturn(domainPage);
    when(transactionMapper.map(domainPage)).thenReturn(expectedResponse);

    // spotless:off
        TransactionPageDTO response =
                given()
                        .standaloneSetup(new TransactionResource(currentUserProvider, transactionService, transactionMapper))
                        .queryParam("type", "EXPENSE")
                .when()
                        .get("/transaction")
                .then()
                        .statusCode(200)
                        .extract().body().as(TransactionPageDTO.class);
        // spotless:on
    assertThat(response.getItems().getFirst().getDescription()).isEqualTo("taxi");
  }

  @Test
  void fetchTransactionBalanceShouldReturnBalanceForCurrentUserAndPeriodTest() {
    UUID currentUserIdValue = UUID.randomUUID();
    UserId currentUserId = new UserId(currentUserIdValue);
    LocalDate startDate = LocalDate.of(2026, 1, 1);
    LocalDate endDate = LocalDate.of(2026, 1, 31);
    MonetaryAmount domainBalance = mock(MonetaryAmount.class);
    MoneyDTO expectedResponse = new MoneyDTO().currency("XAF").value(new BigDecimal("21249.50"));

    when(currentUserProvider.requireCurrentUserId()).thenReturn(currentUserIdValue);
    when(transactionService.getBalance(startDate, endDate, currentUserId))
        .thenReturn(domainBalance);
    when(transactionMapper.map(domainBalance)).thenReturn(expectedResponse);

    // spotless:off
        MoneyDTO response =
                given()
                        .standaloneSetup(new TransactionResource(currentUserProvider, transactionService, transactionMapper))
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
    UUID currentUserIdValue = UUID.randomUUID();
    UserId currentUserId = new UserId(currentUserIdValue);
    LocalDate startDate = LocalDate.of(2026, 2, 1);
    LocalDate endDate = LocalDate.of(2026, 2, 28);
    MonetaryAmount domainAmount = mock(MonetaryAmount.class);
    MoneyDTO expectedResponse = new MoneyDTO().currency("XAF").value(new BigDecimal("4250.75"));

    when(currentUserProvider.requireCurrentUserId()).thenReturn(currentUserIdValue);
    when(transactionMapper.map(TransactionTypeDTO.EXPENSE)).thenReturn(TransactionType.EXPENSE);
    when(transactionService.getAmountByType(
            eq(startDate), eq(endDate), eq(TransactionType.EXPENSE), eq(currentUserId)))
        .thenReturn(domainAmount);
    when(transactionMapper.map(domainAmount)).thenReturn(expectedResponse);

    // spotless:off
        MoneyDTO response =
                given()
                        .standaloneSetup(new TransactionResource(currentUserProvider, transactionService, transactionMapper))
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
