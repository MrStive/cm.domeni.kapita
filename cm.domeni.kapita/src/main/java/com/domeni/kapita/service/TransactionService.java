package com.domeni.kapita.service;

import cm.domeni.generated.domeni.kapita.dto.CreateTransactionDTO;
import cm.domeni.generated.domeni.kapita.dto.MoneyDTO;
import com.domeni.kapita.domain.transaction.Transaction;
import com.domeni.kapita.domain.transaction.TransactionFactory;
import com.domeni.kapita.domain.transaction.TransactionFetcher;
import com.domeni.kapita.domain.transaction.TransactionType;
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
  private final TransactionFetcher transactionFetcher;
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
  public MoneyDTO getBalance(LocalDate startDate, LocalDate endDate, UserId currentUserId) {
    return transactionMapper.map(transactionFetcher.getBalance(startDate, endDate, currentUserId));
  }

  @Transactional(readOnly = true)
  public MoneyDTO getAmountByType(
      LocalDate startDate, LocalDate endDate, TransactionType type, UserId currentUserId) {
    return transactionMapper.map(
        transactionFetcher.getAmount(startDate, endDate, type, currentUserId));
  }
}
