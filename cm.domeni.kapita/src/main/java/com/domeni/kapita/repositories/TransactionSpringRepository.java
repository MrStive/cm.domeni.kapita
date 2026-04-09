package com.domeni.kapita.repositories;

import com.domeni.kapita.domain.transaction.Transaction;
import com.domeni.kapita.domain.transaction.TransactionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionSpringRepository extends JpaRepository<Transaction, TransactionId> {}
