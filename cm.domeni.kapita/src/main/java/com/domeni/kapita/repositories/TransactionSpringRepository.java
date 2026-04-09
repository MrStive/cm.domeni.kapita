package com.domeni.kapita.repositories;

import com.domeni.kapita.domain.transaction.Transaction;
import com.domeni.kapita.domain.transaction.TransactionId;
import com.domeni.kapita.domain.user.UserId;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionSpringRepository extends JpaRepository<Transaction, TransactionId> {

  List<Transaction> findAllByUserIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
      UserId userId, LocalDateTime startInclusive, LocalDateTime endExclusive);
}
