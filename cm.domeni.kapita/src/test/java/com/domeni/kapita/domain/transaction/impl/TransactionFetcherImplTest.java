package com.domeni.kapita.domain.transaction.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verifyNoInteractions;

import com.domeni.kapita.domain.exception.InvalidTransactionPayloadException;
import com.domeni.kapita.domain.transaction.Transaction;
import com.domeni.kapita.domain.transaction.TransactionRepository;
import com.domeni.kapita.domain.transaction.TransactionType;
import com.domeni.kapita.domain.user.UserId;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import javax.money.MonetaryAmount;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionFetcherImplTest {

  @Mock private TransactionRepository transactionRepository;

  @Test
  void getBalanceShouldReturnIncomingMinusExpensesWithinRequestedPeriodTest() {
    TransactionFetcherImpl objectUnderTest = new TransactionFetcherImpl(transactionRepository);
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

    MonetaryAmount result = objectUnderTest.getBalance(startDate, endDate, currentUserId);

    assertThat(result.getCurrency().getCurrencyCode()).isEqualTo("XAF");
    assertThat(result.getNumber().numberValue(BigDecimal.class)).isEqualByComparingTo("320749.25");
    then(transactionRepository)
        .should()
        .findAllByUserIdAndCreatedAtRange(
            currentUserId, expectedStartInclusive, expectedEndExclusive);
  }

  @Test
  void getBalanceWhenNoTransactionMatchesPeriodShouldReturnZeroInXafTest() {
    TransactionFetcherImpl objectUnderTest = new TransactionFetcherImpl(transactionRepository);
    LocalDate startDate = LocalDate.of(2026, 2, 1);
    LocalDate endDate = LocalDate.of(2026, 2, 28);
    LocalDateTime expectedStartInclusive = LocalDateTime.of(2026, 2, 1, 0, 0);
    LocalDateTime expectedEndExclusive = LocalDateTime.of(2026, 3, 1, 0, 0);
    UserId currentUserId = new UserId(UUID.randomUUID());

    given(
            transactionRepository.findAllByUserIdAndCreatedAtRange(
                currentUserId, expectedStartInclusive, expectedEndExclusive))
        .willReturn(List.of());

    MonetaryAmount result = objectUnderTest.getBalance(startDate, endDate, currentUserId);

    assertThat(result.getCurrency().getCurrencyCode()).isEqualTo("XAF");
    assertThat(result.getNumber().numberValue(BigDecimal.class)).isEqualByComparingTo("0");
  }

  @Test
  void getBalanceWhenPeriodIsInvalidShouldThrowDomainExceptionTest() {
    TransactionFetcherImpl objectUnderTest = new TransactionFetcherImpl(transactionRepository);
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
    TransactionFetcherImpl objectUnderTest = new TransactionFetcherImpl(transactionRepository);

    assertThatThrownBy(() -> objectUnderTest.getBalance(LocalDate.now(), LocalDate.now(), null))
        .isInstanceOf(InvalidTransactionPayloadException.class)
        .hasMessage("transaction user id is required");

    verifyNoInteractions(transactionRepository);
  }

  @Test
  void getAmountShouldReturnSumForRequestedTypeWithinRequestedPeriodTest() {
    TransactionFetcherImpl objectUnderTest = new TransactionFetcherImpl(transactionRepository);
    LocalDate startDate = LocalDate.of(2026, 2, 1);
    LocalDate endDate = LocalDate.of(2026, 2, 28);
    LocalDateTime expectedStartInclusive = LocalDateTime.of(2026, 2, 1, 0, 0);
    LocalDateTime expectedEndExclusive = LocalDateTime.of(2026, 3, 1, 0, 0);
    UserId currentUserId = new UserId(UUID.randomUUID());
    Transaction transportTransaction =
        Transaction.builder()
            .type(TransactionType.EXPENSE)
            .amount(new BigDecimal("4250.75"))
            .build();
    Transaction foodTransaction =
        Transaction.builder()
            .type(TransactionType.EXPENSE)
            .amount(new BigDecimal("5750.25"))
            .build();

    given(
            transactionRepository.findAllByUserIdAndTypeAndCreatedAtRange(
                currentUserId,
                TransactionType.EXPENSE,
                expectedStartInclusive,
                expectedEndExclusive))
        .willReturn(List.of(transportTransaction, foodTransaction));

    MonetaryAmount result =
        objectUnderTest.getAmount(startDate, endDate, TransactionType.EXPENSE, currentUserId);

    assertThat(result.getCurrency().getCurrencyCode()).isEqualTo("XAF");
    assertThat(result.getNumber().numberValue(BigDecimal.class)).isEqualByComparingTo("10001.00");
    then(transactionRepository)
        .should()
        .findAllByUserIdAndTypeAndCreatedAtRange(
            currentUserId, TransactionType.EXPENSE, expectedStartInclusive, expectedEndExclusive);
  }

  @Test
  void getAmountWhenNoTransactionMatchesShouldReturnZeroInXafTest() {
    TransactionFetcherImpl objectUnderTest = new TransactionFetcherImpl(transactionRepository);
    LocalDate startDate = LocalDate.of(2026, 2, 1);
    LocalDate endDate = LocalDate.of(2026, 2, 28);
    LocalDateTime expectedStartInclusive = LocalDateTime.of(2026, 2, 1, 0, 0);
    LocalDateTime expectedEndExclusive = LocalDateTime.of(2026, 3, 1, 0, 0);
    UserId currentUserId = new UserId(UUID.randomUUID());

    given(
            transactionRepository.findAllByUserIdAndTypeAndCreatedAtRange(
                currentUserId,
                TransactionType.INCOMING,
                expectedStartInclusive,
                expectedEndExclusive))
        .willReturn(List.of());

    MonetaryAmount result =
        objectUnderTest.getAmount(startDate, endDate, TransactionType.INCOMING, currentUserId);

    assertThat(result.getCurrency().getCurrencyCode()).isEqualTo("XAF");
    assertThat(result.getNumber().numberValue(BigDecimal.class)).isEqualByComparingTo("0");
  }

  @Test
  void getAmountWhenTypeIsMissingShouldThrowDomainExceptionTest() {
    TransactionFetcherImpl objectUnderTest = new TransactionFetcherImpl(transactionRepository);

    assertThatThrownBy(
            () ->
                objectUnderTest.getAmount(
                    LocalDate.now(), LocalDate.now(), null, new UserId(UUID.randomUUID())))
        .isInstanceOf(InvalidTransactionPayloadException.class)
        .hasMessage("transaction type is required");

    verifyNoInteractions(transactionRepository);
  }
}
