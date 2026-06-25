package com.domeni.kapita.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import com.domeni.kapita.domain.transaction.Transaction;
import com.domeni.kapita.domain.transaction.TransactionData;
import com.domeni.kapita.domain.transaction.TransactionFactory;
import com.domeni.kapita.domain.transaction.TransactionFetcher;
import com.domeni.kapita.domain.transaction.TransactionId;
import com.domeni.kapita.domain.transaction.TransactionPage;
import com.domeni.kapita.domain.transaction.TransactionType;
import com.domeni.kapita.domain.user.UserId;
import java.time.LocalDate;
import java.util.UUID;
import javax.money.MonetaryAmount;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

  @Mock private TransactionFactory transactionFactory;
  @Mock private TransactionFetcher transactionFetcher;

  @InjectMocks private TransactionService transactionService;

  @Test
  void createTransactionShouldDelegateDataToFactoryAndReturnCreatedTransactionIdTest() {
    UserId currentUserId = new UserId(UUID.randomUUID());
    TransactionData input = TransactionData.builder().build();
    UUID expectedId = UUID.randomUUID();
    Transaction createdTransaction = new Transaction();
    createdTransaction.setId(new TransactionId(expectedId));

    given(transactionFactory.create(input, currentUserId)).willReturn(createdTransaction);

    UUID result = transactionService.createTransaction(input, currentUserId);

    assertThat(result).isEqualTo(expectedId);
    then(transactionFactory).should().create(input, currentUserId);
  }

  @Test
  void
      createTransactionWhenFactoryReturnsTransactionWithoutIdShouldThrowIllegalStateExceptionTest() {
    UserId currentUserId = new UserId(UUID.randomUUID());
    TransactionData input = TransactionData.builder().build();
    Transaction createdTransaction = new Transaction();
    createdTransaction.setId(null);
    given(transactionFactory.create(input, currentUserId)).willReturn(createdTransaction);

    assertThatThrownBy(() -> transactionService.createTransaction(input, currentUserId))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("created transaction has no identifier");

    then(transactionFactory).should().create(input, currentUserId);
  }

  @Test
  void getTransactionsShouldDelegateToDomainFetcherTest() {
    UserId currentUserId = new UserId(UUID.randomUUID());
    TransactionPage domainPage = new TransactionPage(java.util.List.of(), 0, 10, 0, 0);

    given(transactionFetcher.getTransactions(null, null, null, currentUserId))
        .willReturn(domainPage);

    TransactionPage result = transactionService.getTransactions(null, null, null, currentUserId);

    assertThat(result).isSameAs(domainPage);
    then(transactionFetcher).should().getTransactions(null, null, null, currentUserId);
  }

  @Test
  void getBalanceShouldReturnBalanceWithinRequestedPeriodTest() {
    LocalDate startDate = LocalDate.of(2026, 2, 1);
    LocalDate endDate = LocalDate.of(2026, 2, 28);
    UserId currentUserId = new UserId(UUID.randomUUID());
    MonetaryAmount domainBalance = mock(MonetaryAmount.class);

    given(transactionFetcher.getBalance(startDate, endDate, currentUserId))
        .willReturn(domainBalance);

    MonetaryAmount result = transactionService.getBalance(startDate, endDate, currentUserId);

    assertThat(result).isSameAs(domainBalance);
    then(transactionFetcher).should().getBalance(startDate, endDate, currentUserId);
  }

  @Test
  void getAmountByTypeShouldDelegateToDomainFetcherTest() {
    LocalDate startDate = LocalDate.of(2026, 2, 1);
    LocalDate endDate = LocalDate.of(2026, 2, 28);
    UserId currentUserId = new UserId(UUID.randomUUID());
    MonetaryAmount domainAmount = mock(MonetaryAmount.class);

    given(transactionFetcher.getAmount(startDate, endDate, TransactionType.EXPENSE, currentUserId))
        .willReturn(domainAmount);

    MonetaryAmount result =
        transactionService.getAmountByType(
            startDate, endDate, TransactionType.EXPENSE, currentUserId);

    assertThat(result).isSameAs(domainAmount);
    then(transactionFetcher)
        .should()
        .getAmount(startDate, endDate, TransactionType.EXPENSE, currentUserId);
  }
}
