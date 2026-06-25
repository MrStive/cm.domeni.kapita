package com.domeni.kapita.service;

import com.domeni.kapita.domain.debt.Debt;
import com.domeni.kapita.domain.debt.DebtData;
import com.domeni.kapita.domain.debt.DebtFactory;
import com.domeni.kapita.domain.debt.DebtFetcher;
import com.domeni.kapita.domain.debt.DebtId;
import com.domeni.kapita.domain.debt.DebtPage;
import com.domeni.kapita.domain.debt.DebtType;
import com.domeni.kapita.domain.debt.DebtUpdater;
import com.domeni.kapita.domain.exception.InvalidDebtPayloadException;
import com.domeni.kapita.domain.user.UserId;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DebtService {
  private final DebtFetcher debtFetcher;
  private final DebtFactory debtFactory;
  private final DebtUpdater debtUpdater;

  @Transactional
  public UUID createDebt(DebtData data, UserId currentUserId) {
    Debt createdDebt = debtFactory.create(data, currentUserId);
    if (createdDebt.getId() == null) {
      throw new IllegalStateException("created debt has no identifier");
    }
    return createdDebt.getId().toUUID();
  }

  @Transactional(readOnly = true)
  public DebtPage getDebtsByType(
      DebtType type, Integer pageNumber, Integer pageSize, UserId currentUserId) {
    return debtFetcher.getByType(type, pageNumber, pageSize, currentUserId);
  }

  @Transactional
  public Debt markDebtAsPaid(UUID debtId, UserId currentUserId) {
    if (debtId == null) {
      throw new InvalidDebtPayloadException("debt id is required");
    }
    return debtUpdater.settle(new DebtId(debtId), currentUserId);
  }
}
