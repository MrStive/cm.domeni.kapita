package com.domeni.kapita.domain.transaction.impl;

import com.domeni.kapita.domain.exception.InvalidTransactionPayloadException;
import com.domeni.kapita.domain.transaction.Transaction;
import com.domeni.kapita.domain.transaction.TransactionBalance;
import com.domeni.kapita.domain.transaction.TransactionBalanceFetcher;
import com.domeni.kapita.domain.transaction.TransactionRepository;
import com.domeni.kapita.domain.transaction.TransactionType;
import com.domeni.kapita.domain.user.UserId;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.money.MonetaryAmount;
import lombok.RequiredArgsConstructor;
import org.javamoney.moneta.Money;

@RequiredArgsConstructor
public class TransactionBalanceFetcherImpl implements TransactionBalanceFetcher {
  static final String DEFAULT_CURRENCY = "XAF";

  private final TransactionRepository transactionRepository;

  @Override
  public TransactionBalance getBalance(
      LocalDate startDate, LocalDate endDate, UserId currentUserId) {
    validateBalancePeriod(startDate, endDate);
    if (currentUserId == null) {
      throw new InvalidTransactionPayloadException("transaction user id is required");
    }

    LocalDateTime startInclusive = startDate.atStartOfDay();
    LocalDateTime endExclusive = endDate.plusDays(1).atStartOfDay();

    MonetaryAmount balance =
        transactionRepository
            .findAllByUserIdAndCreatedAtRange(currentUserId, startInclusive, endExclusive)
            .stream()
            .map(this::toSignedAmount)
            .reduce(Money.of(BigDecimal.ZERO, DEFAULT_CURRENCY), MonetaryAmount::add);

    return new TransactionBalance(startDate, endDate, balance);
  }

  private void validateBalancePeriod(LocalDate startDate, LocalDate endDate) {
    if (startDate == null || endDate == null) {
      throw new InvalidTransactionPayloadException("transaction period is required");
    }
    if (endDate.isBefore(startDate)) {
      throw new InvalidTransactionPayloadException("transaction period is invalid");
    }
  }

  private MonetaryAmount toSignedAmount(Transaction transaction) {
    MonetaryAmount amount = Money.of(transaction.getAmount(), DEFAULT_CURRENCY);
    return transaction.getType() == TransactionType.INCOMING ? amount : amount.negate();
  }
}
