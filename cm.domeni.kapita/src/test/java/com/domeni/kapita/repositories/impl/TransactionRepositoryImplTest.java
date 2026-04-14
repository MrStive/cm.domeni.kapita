package com.domeni.kapita.repositories.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import com.domeni.kapita.domain.transaction.Transaction;
import com.domeni.kapita.domain.transaction.TransactionPage;
import com.domeni.kapita.domain.transaction.TransactionType;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

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
  void findAllByUserIdShouldDelegateToSpringRepositoryAndMapPageTest() {
    UserId userId = new UserId(UUID.randomUUID());
    List<Transaction> persistedTransactions =
        List.of(mock(Transaction.class), mock(Transaction.class));
    PageRequest pageable =
        PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, Transaction.Fields.createdAt));

    given(transactionSpringRepository.findAllByUserId(userId, pageable))
        .willReturn(new PageImpl<>(persistedTransactions, pageable, 2));

    TransactionPage result = objectUnderTest.findAllByUserId(userId, 0, 10);

    assertThat(result.items()).containsExactlyElementsOf(persistedTransactions);
    assertThat(result.pageNumber()).isEqualTo(0);
    assertThat(result.pageSize()).isEqualTo(10);
    assertThat(result.totalElements()).isEqualTo(2);
    assertThat(result.totalPages()).isEqualTo(1);
    then(transactionSpringRepository).should().findAllByUserId(userId, pageable);
  }

  @Test
  void findAllByUserIdAndTypeShouldDelegateToSpringRepositoryAndMapPageTest() {
    UserId userId = new UserId(UUID.randomUUID());
    List<Transaction> persistedTransactions =
        List.of(mock(Transaction.class), mock(Transaction.class));
    PageRequest pageable =
        PageRequest.of(1, 5, Sort.by(Sort.Direction.DESC, Transaction.Fields.createdAt));

    given(
            transactionSpringRepository.findAllByUserIdAndType(
                userId, TransactionType.EXPENSE, pageable))
        .willReturn(new PageImpl<>(persistedTransactions, pageable, 7));

    TransactionPage result =
        objectUnderTest.findAllByUserIdAndType(userId, TransactionType.EXPENSE, 1, 5);

    assertThat(result.items()).containsExactlyElementsOf(persistedTransactions);
    assertThat(result.pageNumber()).isEqualTo(1);
    assertThat(result.pageSize()).isEqualTo(5);
    assertThat(result.totalElements()).isEqualTo(7);
    assertThat(result.totalPages()).isEqualTo(2);
    then(transactionSpringRepository)
        .should()
        .findAllByUserIdAndType(userId, TransactionType.EXPENSE, pageable);
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

  @Test
  void findAllByUserIdAndTypeAndCreatedAtRangeShouldDelegateToSpringRepositoryTest() {
    UserId userId = new UserId(UUID.randomUUID());
    LocalDateTime startInclusive = LocalDateTime.of(2026, 1, 1, 0, 0);
    LocalDateTime endExclusive = LocalDateTime.of(2026, 2, 1, 0, 0);
    List<Transaction> persistedTransactions =
        List.of(mock(Transaction.class), mock(Transaction.class));
    given(
            transactionSpringRepository
                .findAllByUserIdAndTypeAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                    userId, TransactionType.EXPENSE, startInclusive, endExclusive))
        .willReturn(persistedTransactions);

    List<Transaction> result =
        objectUnderTest.findAllByUserIdAndTypeAndCreatedAtRange(
            userId, TransactionType.EXPENSE, startInclusive, endExclusive);

    assertThat(result).isSameAs(persistedTransactions);
    then(transactionSpringRepository)
        .should()
        .findAllByUserIdAndTypeAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            userId, TransactionType.EXPENSE, startInclusive, endExclusive);
  }
}
