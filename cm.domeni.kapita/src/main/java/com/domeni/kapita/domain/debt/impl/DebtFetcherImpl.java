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
  static final int DEFAULT_PAGE_NUMBER = 0;
  static final int DEFAULT_PAGE_SIZE = 10;

  private final DebtRepository debtRepository;

  @Override
  public DebtPage getByType(
      DebtType type, Integer pageNumber, Integer pageSize, UserId currentUserId) {
    validateQuery(pageNumber, pageSize, currentUserId);
    int normalizedPageNumber = pageNumber == null ? DEFAULT_PAGE_NUMBER : pageNumber;
    int normalizedPageSize = pageSize == null ? DEFAULT_PAGE_SIZE : pageSize;

    if (type == null) {
      return debtRepository.findAllByUserId(
          currentUserId, normalizedPageNumber, normalizedPageSize);
    }
    return debtRepository.findAllByUserIdAndType(
        currentUserId, type, normalizedPageNumber, normalizedPageSize);
  }

  private void validateQuery(Integer pageNumber, Integer pageSize, UserId currentUserId) {
    if (pageNumber != null && pageNumber < 0) {
      throw new InvalidDebtPayloadException("debt page number is invalid");
    }
    if (pageSize != null && pageSize <= 0) {
      throw new InvalidDebtPayloadException("debt page size is invalid");
    }
    if (currentUserId == null) {
      throw new InvalidDebtPayloadException("debt user id is required");
    }
  }
}
