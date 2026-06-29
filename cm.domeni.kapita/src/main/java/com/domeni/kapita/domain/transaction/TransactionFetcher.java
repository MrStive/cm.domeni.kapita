package com.domeni.kapita.domain.transaction;

import com.domeni.kapita.domain.user.UserId;
import java.time.LocalDate;
import java.util.Map;
import javax.money.MonetaryAmount;

public interface TransactionFetcher {

  TransactionPage getTransactions(
      TransactionType type, Integer pageNumber, Integer pageSize, UserId currentUserId);

  MonetaryAmount getBalance(LocalDate startDate, LocalDate endDate, UserId currentUserId);

  MonetaryAmount getAmount(
      LocalDate startDate, LocalDate endDate, TransactionType type, UserId currentUserId);

  Map<TransactionType, MonetaryAmount> getAmountsGroupedByType(
      LocalDate startDate, LocalDate endDate, UserId currentUserId);
}
