package com.domeni.kapita.repositories.impl;

import com.domeni.kapita.domain.transaction.Transaction;
import com.domeni.kapita.domain.transaction.TransactionRepository;
import com.domeni.kapita.repositories.TransactionSpringRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TransactionRepositoryImpl implements TransactionRepository {
  private final TransactionSpringRepository transactionSpringRepository;

  @Override
  public Transaction save(Transaction value) {
    return transactionSpringRepository.save(value);
  }
}
