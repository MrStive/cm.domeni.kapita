package com.domeni.kapita.repositories;

import com.domeni.kapita.domain.transaction.Transaction;
import com.domeni.kapita.domain.transaction.TransactionId;
import com.domeni.kapita.domain.transaction.TransactionType;
import com.domeni.kapita.domain.user.UserId;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionSpringRepository extends JpaRepository<Transaction, TransactionId> {

  Page<Transaction> findAllByUserId(UserId userId, Pageable pageable);

  Page<Transaction> findAllByUserIdAndType(UserId userId, TransactionType type, Pageable pageable);

  List<Transaction> findAllByUserIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
      UserId userId, LocalDateTime startInclusive, LocalDateTime endExclusive);

  List<Transaction> findAllByUserIdAndTypeAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
      UserId userId,
      TransactionType type,
      LocalDateTime startInclusive,
      LocalDateTime endExclusive);
}
