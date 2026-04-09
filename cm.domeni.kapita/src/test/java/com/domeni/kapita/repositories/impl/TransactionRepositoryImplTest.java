package com.domeni.kapita.repositories.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import com.domeni.kapita.domain.transaction.Transaction;
import com.domeni.kapita.repositories.TransactionSpringRepository;
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
}
