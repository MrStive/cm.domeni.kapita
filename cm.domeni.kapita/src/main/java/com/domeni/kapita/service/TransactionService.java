package com.domeni.kapita.service;

import cm.domeni.generated.domeni.kapita.dto.CreateTransactionDTO;
import cm.domeni.generated.domeni.kapita.dto.TransactionBalanceDTO;
import com.domeni.kapita.domain.transaction.Transaction;
import com.domeni.kapita.domain.transaction.TransactionBalanceFetcher;
import com.domeni.kapita.domain.transaction.TransactionFactory;
import com.domeni.kapita.domain.user.UserId;
import com.domeni.kapita.service.mapper.TransactionMapper;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionService {
  private final TransactionFactory transactionFactory;
  private final TransactionBalanceFetcher transactionBalanceFetcher;
  private final TransactionMapper transactionMapper;

  @Transactional
  public UUID createTransaction(CreateTransactionDTO data, UserId currentUserId) {
    Transaction createdTransaction =
        transactionFactory.create(transactionMapper.map(data), currentUserId);
    if (createdTransaction.getId() == null) {
      throw new IllegalStateException("created transaction has no identifier");
    }
    return createdTransaction.getId().toUUID();
  }

  @Transactional(readOnly = true)
  public TransactionBalanceDTO getBalance(
      LocalDate startDate, LocalDate endDate, UserId currentUserId) {
    return transactionMapper.map(
        transactionBalanceFetcher.getBalance(startDate, endDate, currentUserId));
  }
}
