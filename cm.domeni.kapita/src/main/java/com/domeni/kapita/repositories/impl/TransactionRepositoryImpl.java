package com.domeni.kapita.repositories.impl;

import com.domeni.kapita.domain.transaction.Transaction;
import com.domeni.kapita.domain.transaction.TransactionPage;
import com.domeni.kapita.domain.transaction.TransactionRepository;
import com.domeni.kapita.domain.transaction.TransactionType;
import com.domeni.kapita.domain.user.UserId;
import com.domeni.kapita.repositories.TransactionSpringRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@RequiredArgsConstructor
public class TransactionRepositoryImpl implements TransactionRepository {
  private final TransactionSpringRepository transactionSpringRepository;

  @Override
  public Transaction save(Transaction value) {
    return transactionSpringRepository.save(value);
  }

  @Override
  public TransactionPage findAllByUserId(UserId userId, int pageNumber, int pageSize) {
    Page<Transaction> page =
        transactionSpringRepository.findAllByUserId(
            userId,
            PageRequest.of(
                pageNumber, pageSize, Sort.by(Sort.Direction.DESC, Transaction.Fields.createdAt)));
    return toPage(page);
  }

  @Override
  public TransactionPage findAllByUserIdAndType(
      UserId userId, TransactionType type, int pageNumber, int pageSize) {
    Page<Transaction> page =
        transactionSpringRepository.findAllByUserIdAndType(
            userId,
            type,
            PageRequest.of(
                pageNumber, pageSize, Sort.by(Sort.Direction.DESC, Transaction.Fields.createdAt)));
    return toPage(page);
  }

  @Override
  public List<Transaction> findAllByUserIdAndCreatedAtRange(
      UserId userId, LocalDateTime startInclusive, LocalDateTime endExclusive) {
    return transactionSpringRepository
        .findAllByUserIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            userId, startInclusive, endExclusive);
  }

  @Override
  public List<Transaction> findAllByUserIdAndTypeAndCreatedAtRange(
      UserId userId,
      TransactionType type,
      LocalDateTime startInclusive,
      LocalDateTime endExclusive) {
    return transactionSpringRepository
        .findAllByUserIdAndTypeAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            userId, type, startInclusive, endExclusive);
  }

  private TransactionPage toPage(Page<Transaction> page) {
    return new TransactionPage(
        page.getContent(),
        page.getNumber(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages());
  }
}
