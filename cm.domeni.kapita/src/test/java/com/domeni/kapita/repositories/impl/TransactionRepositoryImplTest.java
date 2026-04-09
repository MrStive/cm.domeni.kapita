package com.domeni.kapita.repositories.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import com.domeni.kapita.domain.transaction.Transaction;
import com.domeni.kapita.domain.user.UserId;
import com.domeni.kapita.repositories.TransactionSpringRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionRepositoryImplTest {

  @InjectMocks private TransactionRepositoryImpl objectUnderTest;

  @Mock private TransactionSpringRepository transactionSpringRepository;

  @Test
  void saveShouldDelegateToSpringRepositoryTest() {
    Transaction transaction = mock(Transaction.class);
    Transaction persistedTransaction = mock(Transaction.class);
    given(transactionSpringRepository.save(transaction)).willReturn(persistedTransaction);

    Transaction result = objectUnderTest.save(transaction);

    assertThat(result).isSameAs(persistedTransaction);
    then(transactionSpringRepository).should().save(transaction);
  }

  @Test
  void findAllByUserIdAndCreatedAtRangeShouldDelegateToSpringRepositoryTest() {
    UserId userId = new UserId(UUID.randomUUID());
    LocalDateTime startInclusive = LocalDateTime.of(2026, 1, 1, 0, 0);
    LocalDateTime endExclusive = LocalDateTime.of(2026, 2, 1, 0, 0);
    List<Transaction> persistedTransactions =
        List.of(mock(Transaction.class), mock(Transaction.class));
    given(
            transactionSpringRepository
                .findAllByUserIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                    userId, startInclusive, endExclusive))
        .willReturn(persistedTransactions);

    List<Transaction> result =
        objectUnderTest.findAllByUserIdAndCreatedAtRange(userId, startInclusive, endExclusive);

    assertThat(result).isSameAs(persistedTransactions);
    then(transactionSpringRepository)
        .should()
        .findAllByUserIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            userId, startInclusive, endExclusive);
  }
}
