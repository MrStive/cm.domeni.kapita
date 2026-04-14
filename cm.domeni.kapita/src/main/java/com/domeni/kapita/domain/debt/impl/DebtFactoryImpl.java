package com.domeni.kapita.domain.debt.impl;

import com.domeni.kapita.domain.debt.Debt;
import com.domeni.kapita.domain.debt.DebtData;
import com.domeni.kapita.domain.debt.DebtFactory;
import com.domeni.kapita.domain.debt.DebtId;
import com.domeni.kapita.domain.debt.DebtRepository;
import com.domeni.kapita.domain.debt.DebtStatus;
import com.domeni.kapita.domain.exception.InvalidDebtPayloadException;
import com.domeni.kapita.domain.user.UserId;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DebtFactoryImpl implements DebtFactory {
  private final DebtRepository debtRepository;
  private final Clock clock;

  @Override
  public Debt create(DebtData debtData, UserId currentUserId) {
    if (currentUserId == null) {
      throw new InvalidDebtPayloadException("debt user id is required");
    }

    DebtData normalizedData = normalize(debtData);
    Debt createdDebt =
        debtRepository.save(
            Debt.builder()
                .id(new DebtId(UUID.randomUUID()))
                .type(normalizedData.type())
                .counterpartyName(normalizedData.counterpartyName())
                .amount(normalizedData.amount())
                .dueDate(normalizedData.dueDate())
                .status(DebtStatus.UNPAID)
                .userId(currentUserId)
                .createdAt(LocalDateTime.now(clock))
                .build());
    return createdDebt;
  }

  private DebtData normalize(DebtData data) {
    return DebtData.builder()
        .type(data.type())
        .counterpartyName(normalizeOptional(data.counterpartyName()))
        .amount(data.amount())
        .dueDate(data.dueDate())
        .build();
  }

  private String normalizeOptional(String value) {
    if (value == null) {
      return null;
    }
    String trimmedValue = value.trim();
    return trimmedValue.isEmpty() ? null : trimmedValue;
  }
}
