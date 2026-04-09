package com.domeni.kapita.api;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import cm.domeni.generated.domeni.kapita.dto.CreateTransactionDTO;
import cm.domeni.generated.domeni.kapita.dto.CreationResponseDTO;
import cm.domeni.generated.domeni.kapita.dto.MoneyDTO;
import cm.domeni.generated.domeni.kapita.dto.TransactionBalanceDTO;
import cm.domeni.generated.domeni.kapita.dto.TransactionCategoryDTO;
import cm.domeni.generated.domeni.kapita.dto.TransactionTypeDTO;
import com.domeni.kapita.domain.user.UserId;
import com.domeni.kapita.security.jwt.CurrentUserProvider;
import com.domeni.kapita.service.TransactionService;
import java.math.BigDecimal;
import java.time.LocalDate;
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
  void fetchTransactionBalanceShouldReturnBalanceForCurrentUserAndPeriodTest() {
    UUID currentUserId = UUID.randomUUID();
    LocalDate startDate = LocalDate.of(2026, 1, 1);
    LocalDate endDate = LocalDate.of(2026, 1, 31);
    TransactionBalanceDTO expectedResponse =
        new TransactionBalanceDTO()
            .startDate(startDate)
            .endDate(endDate)
            .balance(new MoneyDTO().currency("XAF").value(new BigDecimal("21249.50")));

    when(currentUserProvider.requireCurrentUserId()).thenReturn(currentUserId);
    when(transactionService.getBalance(startDate, endDate, new UserId(currentUserId)))
        .thenReturn(expectedResponse);

    // spotless:off
        TransactionBalanceDTO response =
                given()
                        .standaloneSetup(new TransactionResource(currentUserProvider, transactionService))
                        .queryParam("startDate", startDate.toString())
                        .queryParam("endDate", endDate.toString())
                .when()
                        .get("/transaction/balance")
                .then()
                        .statusCode(200)
                        .extract().body().as(TransactionBalanceDTO.class);
        // spotless:on
    assertThat(response.getStartDate()).isEqualTo(startDate);
    assertThat(response.getEndDate()).isEqualTo(endDate);
    assertThat(response.getBalance().getCurrency()).isEqualTo("XAF");
    assertThat(response.getBalance().getValue()).isEqualByComparingTo("21249.50");
  }
}
