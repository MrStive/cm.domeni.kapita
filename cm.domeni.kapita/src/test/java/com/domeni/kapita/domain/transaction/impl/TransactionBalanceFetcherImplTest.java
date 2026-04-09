package com.domeni.kapita.domain.transaction.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verifyNoInteractions;

import com.domeni.kapita.domain.exception.InvalidTransactionPayloadException;
import com.domeni.kapita.domain.transaction.Transaction;
import com.domeni.kapita.domain.transaction.TransactionBalance;
import com.domeni.kapita.domain.transaction.TransactionRepository;
import com.domeni.kapita.domain.transaction.TransactionType;
import com.domeni.kapita.domain.user.UserId;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionBalanceFetcherImplTest {

  @Mock private TransactionRepository transactionRepository;

  @Test
  void getBalanceShouldReturnIncomingMinusExpensesWithinRequestedPeriodTest() {
    TransactionBalanceFetcherImpl objectUnderTest =
        new TransactionBalanceFetcherImpl(transactionRepository);
    LocalDate startDate = LocalDate.of(2026, 2, 1);
    LocalDate endDate = LocalDate.of(2026, 2, 28);
    LocalDateTime expectedStartInclusive = LocalDateTime.of(2026, 2, 1, 0, 0);
    LocalDateTime expectedEndExclusive = LocalDateTime.of(2026, 3, 1, 0, 0);
    UserId currentUserId = new UserId(UUID.randomUUID());
    Transaction salaryTransaction =
        Transaction.builder()
            .type(TransactionType.INCOMING)
            .amount(new BigDecimal("300000.00"))
            .build();
    Transaction saleTransaction =
        Transaction.builder()
            .type(TransactionType.INCOMING)
            .amount(new BigDecimal("25000.00"))
            .build();
    Transaction transportTransaction =
        Transaction.builder()
            .type(TransactionType.EXPENSE)
            .amount(new BigDecimal("4250.75"))
            .build();

    given(
            transactionRepository.findAllByUserIdAndCreatedAtRange(
                currentUserId, expectedStartInclusive, expectedEndExclusive))
        .willReturn(List.of(salaryTransaction, saleTransaction, transportTransaction));

    TransactionBalance result = objectUnderTest.getBalance(startDate, endDate, currentUserId);

    assertThat(result.startDate()).isEqualTo(startDate);
    assertThat(result.endDate()).isEqualTo(endDate);
    assertThat(result.balance().getCurrency().getCurrencyCode()).isEqualTo("XAF");
    assertThat(result.balance().getNumber().numberValue(BigDecimal.class))
        .isEqualByComparingTo("320749.25");
    then(transactionRepository)
        .should()
        .findAllByUserIdAndCreatedAtRange(
            currentUserId, expectedStartInclusive, expectedEndExclusive);
  }

  @Test
  void getBalanceWhenNoTransactionMatchesPeriodShouldReturnZeroInXafTest() {
    TransactionBalanceFetcherImpl objectUnderTest =
        new TransactionBalanceFetcherImpl(transactionRepository);
    LocalDate startDate = LocalDate.of(2026, 2, 1);
    LocalDate endDate = LocalDate.of(2026, 2, 28);
    LocalDateTime expectedStartInclusive = LocalDateTime.of(2026, 2, 1, 0, 0);
    LocalDateTime expectedEndExclusive = LocalDateTime.of(2026, 3, 1, 0, 0);
    UserId currentUserId = new UserId(UUID.randomUUID());

    given(
            transactionRepository.findAllByUserIdAndCreatedAtRange(
                currentUserId, expectedStartInclusive, expectedEndExclusive))
        .willReturn(List.of());

    TransactionBalance result = objectUnderTest.getBalance(startDate, endDate, currentUserId);

    assertThat(result.balance().getCurrency().getCurrencyCode()).isEqualTo("XAF");
    assertThat(result.balance().getNumber().numberValue(BigDecimal.class))
        .isEqualByComparingTo("0");
  }

  @Test
  void getBalanceWhenPeriodIsInvalidShouldThrowDomainExceptionTest() {
    TransactionBalanceFetcherImpl objectUnderTest =
        new TransactionBalanceFetcherImpl(transactionRepository);
    UserId currentUserId = new UserId(UUID.randomUUID());

    assertThatThrownBy(
            () ->
                objectUnderTest.getBalance(
                    LocalDate.of(2026, 3, 2), LocalDate.of(2026, 3, 1), currentUserId))
        .isInstanceOf(InvalidTransactionPayloadException.class)
        .hasMessage("transaction period is invalid");

    verifyNoInteractions(transactionRepository);
  }

  @Test
  void getBalanceWhenCurrentUserIdIsMissingShouldThrowDomainExceptionTest() {
    TransactionBalanceFetcherImpl objectUnderTest =
        new TransactionBalanceFetcherImpl(transactionRepository);

    assertThatThrownBy(() -> objectUnderTest.getBalance(LocalDate.now(), LocalDate.now(), null))
        .isInstanceOf(InvalidTransactionPayloadException.class)
        .hasMessage("transaction user id is required");

    verifyNoInteractions(transactionRepository);
  }
}
