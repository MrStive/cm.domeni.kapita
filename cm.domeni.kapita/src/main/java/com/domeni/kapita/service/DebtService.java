package com.domeni.kapita.service;

import cm.domeni.generated.domeni.kapita.dto.CreateDebtDTO;
import cm.domeni.generated.domeni.kapita.dto.DebtDTO;
import cm.domeni.generated.domeni.kapita.dto.DebtPageDTO;
import com.domeni.kapita.domain.debt.Debt;
import com.domeni.kapita.domain.debt.DebtFactory;
import com.domeni.kapita.domain.debt.DebtFetcher;
import com.domeni.kapita.domain.debt.DebtId;
import com.domeni.kapita.domain.debt.DebtType;
import com.domeni.kapita.domain.debt.DebtUpdater;
import com.domeni.kapita.domain.exception.InvalidDebtPayloadException;
import com.domeni.kapita.domain.user.UserId;
import com.domeni.kapita.service.mapper.DebtMapper;
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
  private final DebtMapper debtMapper;

  @Transactional
  public UUID createDebt(CreateDebtDTO data, UserId currentUserId) {
    Debt createdDebt = debtFactory.create(debtMapper.map(data), currentUserId);
    if (createdDebt.getId() == null) {
      throw new IllegalStateException("created debt has no identifier");
    }
    return createdDebt.getId().toUUID();
  }

  @Transactional(readOnly = true)
  public DebtPageDTO getDebtsByType(
      DebtType type, Integer pageNumber, Integer pageSize, UserId currentUserId) {
    return debtMapper.map(debtFetcher.getByType(type, pageNumber, pageSize, currentUserId));
  }

  @Transactional
  public DebtDTO markDebtAsPaid(UUID debtId, UserId currentUserId) {
    if (debtId == null) {
      throw new InvalidDebtPayloadException("debt id is required");
    }
    return debtMapper.map(debtUpdater.settle(new DebtId(debtId), currentUserId));
  }
}
