package com.domeni.kapita.domain.transaction;

import com.domeni.kapita.domain.user.UserId;
import java.time.LocalDate;

public interface TransactionBalanceFetcher {

  TransactionBalance getBalance(LocalDate startDate, LocalDate endDate, UserId currentUserId);
}
