package com.domeni.kapita.domain.debt.impl;

import com.domeni.kapita.domain.debt.Debt;
import com.domeni.kapita.domain.debt.DebtId;
import com.domeni.kapita.domain.debt.DebtRepository;
import com.domeni.kapita.domain.debt.DebtStatus;
import com.domeni.kapita.domain.debt.DebtType;
import com.domeni.kapita.domain.debt.DebtUpdater;
import com.domeni.kapita.domain.exception.DebtNotFoundException;
import com.domeni.kapita.domain.exception.InvalidDebtPayloadException;
import com.domeni.kapita.domain.transaction.TransactionCategory;
import com.domeni.kapita.domain.transaction.TransactionData;
import com.domeni.kapita.domain.transaction.TransactionFactory;
import com.domeni.kapita.domain.transaction.TransactionType;
import com.domeni.kapita.domain.user.UserId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DebtUpdaterImpl implements DebtUpdater {
  static final String TRANSACTION_OTHER_CATEGORY_DETAIL = "debt";

  private final DebtRepository debtRepository;
  private final TransactionFactory transactionFactory;

  @Override
  public Debt settle(DebtId debtId, UserId currentUserId) {
    if (debtId == null) {
      throw new InvalidDebtPayloadException("debt id is required");
    }
    if (currentUserId == null) {
      throw new InvalidDebtPayloadException("debt user id is required");
    }

    Debt debt =
        debtRepository
            .findByIdAndUserId(debtId, currentUserId)
            .orElseThrow(DebtNotFoundException::new);

    if (debt.getStatus() == DebtStatus.PAID) {
      return debt;
    }

    debt.setStatus(DebtStatus.PAID);
    Debt settledDebt = debtRepository.save(debt);
    transactionFactory.create(toTransactionData(settledDebt), currentUserId);
    return settledDebt;
  }

  private TransactionData toTransactionData(Debt debt) {
    return TransactionData.builder()
        .type(toTransactionType(debt.getType()))
        .category(TransactionCategory.OTHER)
        .otherCategoryDetail(TRANSACTION_OTHER_CATEGORY_DETAIL)
        .amount(debt.getAmount().getNumber().numberValueExact(java.math.BigDecimal.class))
        .description(debt.getCounterpartyName())
        .build();
  }

  private TransactionType toTransactionType(DebtType debtType) {
    return switch (debtType) {
      case OWED_TO_ME -> TransactionType.INCOMING;
      case OWED_BY_ME -> TransactionType.EXPENSE;
    };
  }
}
