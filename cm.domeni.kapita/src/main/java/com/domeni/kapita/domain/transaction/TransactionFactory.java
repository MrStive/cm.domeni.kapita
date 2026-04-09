package com.domeni.kapita.domain.transaction;

import com.domeni.kapita.domain.user.UserId;

public interface TransactionFactory {

  Transaction create(TransactionData transactionData, UserId currentUserId);
}
