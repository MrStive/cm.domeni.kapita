package com.domeni.kapita.domain.debt.impl;

import com.domeni.kapita.domain.debt.DebtFetcher;
import com.domeni.kapita.domain.debt.DebtPage;
import com.domeni.kapita.domain.debt.DebtRepository;
import com.domeni.kapita.domain.debt.DebtType;
import com.domeni.kapita.domain.exception.InvalidDebtPayloadException;
import com.domeni.kapita.domain.user.UserId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DebtFetcherImpl implements DebtFetcher {
  private final DebtRepository debtRepository;

  @Override
  public DebtPage getByType(
      DebtType type, Integer pageNumber, Integer pageSize, UserId currentUserId) {
    validateQuery(type, pageNumber, pageSize, currentUserId);
    return debtRepository.findAllByUserIdAndType(currentUserId, type, pageNumber, pageSize);
  }

  private void validateQuery(
      DebtType type, Integer pageNumber, Integer pageSize, UserId currentUserId) {
    if (pageNumber == null || pageNumber < 0) {
      throw new InvalidDebtPayloadException("debt page number is invalid");
    }
    if (pageSize == null || pageSize <= 0) {
      throw new InvalidDebtPayloadException("debt page size is invalid");
    }
    if (currentUserId == null) {
      throw new InvalidDebtPayloadException("debt user id is required");
    }
  }
}
