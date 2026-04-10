package com.domeni.kapita.service;

import cm.domeni.generated.domeni.kapita.dto.CreateDebtDTO;
import com.domeni.kapita.domain.debt.Debt;
import com.domeni.kapita.domain.debt.DebtFactory;
import com.domeni.kapita.domain.user.UserId;
import com.domeni.kapita.service.mapper.DebtMapper;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DebtService {
  private final DebtFactory debtFactory;
  private final DebtMapper debtMapper;

  @Transactional
  public UUID createDebt(CreateDebtDTO data, UserId currentUserId) {
    Debt createdDebt = debtFactory.create(debtMapper.map(data), currentUserId);
    if (createdDebt.getId() == null) {
      throw new IllegalStateException("created debt has no identifier");
    }
    return createdDebt.getId().toUUID();
  }
}
