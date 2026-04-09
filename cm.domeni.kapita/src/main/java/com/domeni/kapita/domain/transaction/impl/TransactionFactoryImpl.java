package com.domeni.kapita.domain.transaction.impl;

import com.domeni.kapita.domain.exception.InvalidTransactionPayloadException;
import com.domeni.kapita.domain.transaction.Transaction;
import com.domeni.kapita.domain.transaction.TransactionCategory;
import com.domeni.kapita.domain.transaction.TransactionData;
import com.domeni.kapita.domain.transaction.TransactionFactory;
import com.domeni.kapita.domain.transaction.TransactionId;
import com.domeni.kapita.domain.transaction.TransactionRepository;
import com.domeni.kapita.domain.user.UserId;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TransactionFactoryImpl implements TransactionFactory {
  private final TransactionRepository transactionRepository;
  private final Clock clock;

  @Override
  public Transaction create(TransactionData transactionData, UserId currentUserId) {
    TransactionData normalizedData = normalizeAndValidate(transactionData);
    if (currentUserId == null) {
      throw new InvalidTransactionPayloadException("transaction user id is required");
    }
    return transactionRepository.save(
        Transaction.builder()
            .id(new TransactionId(UUID.randomUUID()))
            .type(normalizedData.type())
            .category(normalizedData.category())
            .otherCategoryDetail(normalizedData.otherCategoryDetail())
            .amount(normalizedData.amount())
            .description(normalizedData.description())
            .userId(currentUserId)
            .createdAt(LocalDateTime.now(clock))
            .build());
  }

  private TransactionData normalizeAndValidate(TransactionData data) {
    if (data == null) {
      throw new InvalidTransactionPayloadException("transaction payload is required");
    }

    if (!isPositiveAmount(data.amount())) {
      throw new InvalidTransactionPayloadException("transaction payload is invalid");
    }
    if (!data.category().supports(data.type())) {
      throw new InvalidTransactionPayloadException("transaction category is invalid for type");
    }

    String normalizedDescription = normalizeOptional(data.description());
    String normalizedOtherCategoryDetail = normalizeOptional(data.otherCategoryDetail());
    if (data.category().requiresOtherCategoryDetail() && normalizedOtherCategoryDetail == null) {
      throw new InvalidTransactionPayloadException("transaction other category detail is required");
    }

    return TransactionData.builder()
        .type(data.type())
        .category(data.category())
        .otherCategoryDetail(
            data.category() == TransactionCategory.OTHER ? normalizedOtherCategoryDetail : null)
        .amount(data.amount())
        .description(normalizedDescription)
        .build();
  }

  private boolean isPositiveAmount(BigDecimal amount) {
    return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
  }

  private String normalizeOptional(String value) {
    if (value == null) {
      return null;
    }
    String trimmedValue = value.trim();
    return trimmedValue.isEmpty() ? null : trimmedValue;
  }
}
