package com.domeni.kapita.domain.transaction.impl;

import com.domeni.kapita.domain.exception.InvalidTransactionPayloadException;
import com.domeni.kapita.domain.transaction.Transaction;
import com.domeni.kapita.domain.transaction.TransactionFetcher;
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
public class TransactionFetcherImpl implements TransactionFetcher {
  static final String DEFAULT_CURRENCY = "XAF";

  private final TransactionRepository transactionRepository;

  @Override
  public MonetaryAmount getBalance(
      LocalDate startDate, LocalDate endDate, UserId currentUserId) {
    validatePeriodAndUser(startDate, endDate, currentUserId);
    LocalDateTime startInclusive = startDate.atStartOfDay();
    LocalDateTime endExclusive = endDate.plusDays(1).atStartOfDay();

    return transactionRepository
        .findAllByUserIdAndCreatedAtRange(currentUserId, startInclusive, endExclusive)
        .stream()
        .map(this::toSignedAmount)
        .reduce(Money.of(BigDecimal.ZERO, DEFAULT_CURRENCY), MonetaryAmount::add);
  }

  @Override
  public MonetaryAmount getAmount(
      LocalDate startDate, LocalDate endDate, TransactionType type, UserId currentUserId) {
    validatePeriodAndUser(startDate, endDate, currentUserId);

    LocalDateTime startInclusive = startDate.atStartOfDay();
    LocalDateTime endExclusive = endDate.plusDays(1).atStartOfDay();

    return transactionRepository
        .findAllByUserIdAndTypeAndCreatedAtRange(currentUserId, type, startInclusive, endExclusive)
        .stream()
        .<MonetaryAmount>map(transaction -> Money.of(transaction.getAmount(), DEFAULT_CURRENCY))
        .reduce(Money.of(BigDecimal.ZERO, DEFAULT_CURRENCY), MonetaryAmount::add);
  }

  private void validatePeriodAndUser(
      LocalDate startDate, LocalDate endDate, UserId currentUserId) {
    if (startDate == null || endDate == null) {
      throw new InvalidTransactionPayloadException("transaction period is required");
    }
    if (endDate.isBefore(startDate)) {
      throw new InvalidTransactionPayloadException("transaction period is invalid");
    }
    if (currentUserId == null) {
      throw new InvalidTransactionPayloadException("transaction user id is required");
    }
  }

  private MonetaryAmount toSignedAmount(Transaction transaction) {
    MonetaryAmount amount = Money.of(transaction.getAmount(), DEFAULT_CURRENCY);
    return transaction.getType() == TransactionType.INCOMING ? amount : amount.negate();
  }
}
