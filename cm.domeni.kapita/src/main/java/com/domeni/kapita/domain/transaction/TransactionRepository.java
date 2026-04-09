package com.domeni.kapita.domain.transaction;

import com.domeni.kapita.domain.user.UserId;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository {

  Transaction save(Transaction value);

  List<Transaction> findAllByUserIdAndCreatedAtRange(
      UserId userId, LocalDateTime startInclusive, LocalDateTime endExclusive);
}
