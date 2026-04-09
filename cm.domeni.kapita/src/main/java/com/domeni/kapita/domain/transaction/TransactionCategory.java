package com.domeni.kapita.domain.transaction;

import java.util.Set;

public enum TransactionCategory {
  STOCK(TransactionType.EXPENSE),
  TRANSPORT(TransactionType.EXPENSE),
  FOOD(TransactionType.EXPENSE),
  PHONE_CREDIT(TransactionType.EXPENSE),
  RENT(TransactionType.EXPENSE),
  EMPLOYEE_SALARY(TransactionType.EXPENSE),
  SALE(TransactionType.INCOMING),
  SERVICE_PROVIDED(TransactionType.INCOMING),
  SALARY(TransactionType.INCOMING),
  OTHER(TransactionType.INCOMING, TransactionType.EXPENSE);

  private final Set<TransactionType> supportedTypes;

  TransactionCategory(TransactionType... supportedTypes) {
    this.supportedTypes = Set.of(supportedTypes);
  }

  public boolean supports(TransactionType transactionType) {
    return transactionType != null && supportedTypes.contains(transactionType);
  }

  public boolean requiresOtherCategoryDetail() {
    return this == OTHER;
  }
}
