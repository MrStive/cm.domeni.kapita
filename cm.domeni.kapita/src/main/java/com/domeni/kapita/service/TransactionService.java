package com.domeni.kapita.service;

import com.domeni.kapita.domain.transaction.Transaction;
import com.domeni.kapita.domain.transaction.TransactionData;
import com.domeni.kapita.domain.transaction.TransactionFactory;
import com.domeni.kapita.domain.transaction.TransactionFetcher;
import com.domeni.kapita.domain.transaction.TransactionPage;
import com.domeni.kapita.domain.transaction.TransactionType;
import com.domeni.kapita.domain.user.UserId;
import java.time.LocalDate;
import java.util.UUID;
import javax.money.MonetaryAmount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionService {
  private final TransactionFactory transactionFactory;
  private final TransactionFetcher transactionFetcher;

  @Transactional
  public UUID createTransaction(TransactionData data, UserId currentUserId) {
    Transaction createdTransaction = transactionFactory.create(data, currentUserId);
    if (createdTransaction.getId() == null) {
      throw new IllegalStateException("created transaction has no identifier");
    }
    return createdTransaction.getId().toUUID();
  }

  @Transactional(readOnly = true)
  public TransactionPage getTransactions(
      TransactionType type, Integer pageNumber, Integer pageSize, UserId currentUserId) {
    return transactionFetcher.getTransactions(type, pageNumber, pageSize, currentUserId);
  }

  @Transactional(readOnly = true)
  public MonetaryAmount getBalance(LocalDate startDate, LocalDate endDate, UserId currentUserId) {
    return transactionFetcher.getBalance(startDate, endDate, currentUserId);
  }

  @Transactional(readOnly = true)
  public MonetaryAmount getAmountByType(
      LocalDate startDate, LocalDate endDate, TransactionType type, UserId currentUserId) {
    return transactionFetcher.getAmount(startDate, endDate, type, currentUserId);
  }
}
