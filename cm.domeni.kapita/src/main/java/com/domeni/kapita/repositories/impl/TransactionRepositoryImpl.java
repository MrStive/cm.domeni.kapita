package com.domeni.kapita.repositories.impl;

import com.domeni.kapita.domain.transaction.Transaction;
import com.domeni.kapita.domain.transaction.TransactionRepository;
import com.domeni.kapita.domain.user.UserId;
import com.domeni.kapita.repositories.TransactionSpringRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TransactionRepositoryImpl implements TransactionRepository {
  private final TransactionSpringRepository transactionSpringRepository;

  @Override
  public Transaction save(Transaction value) {
    return transactionSpringRepository.save(value);
  }

  @Override
  public List<Transaction> findAllByUserIdAndCreatedAtRange(
      UserId userId, LocalDateTime startInclusive, LocalDateTime endExclusive) {
    return transactionSpringRepository
        .findAllByUserIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            userId, startInclusive, endExclusive);
  }
}
