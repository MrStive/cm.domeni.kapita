package com.domeni.kapita.domain.transaction;

import com.domeni.kapita.domain.user.UserId;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository {

  Transaction save(Transaction value);

  TransactionPage findAllByUserId(UserId userId, int pageNumber, int pageSize);

  TransactionPage findAllByUserIdAndType(
      UserId userId, TransactionType type, int pageNumber, int pageSize);

  List<Transaction> findAllByUserIdAndCreatedAtRange(
      UserId userId, LocalDateTime startInclusive, LocalDateTime endExclusive);

  List<Transaction> findAllByUserIdAndTypeAndCreatedAtRange(
      UserId userId,
      TransactionType type,
      LocalDateTime startInclusive,
      LocalDateTime endExclusive);
}
