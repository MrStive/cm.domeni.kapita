package com.domeni.kapita.repositories.impl;

import com.domeni.kapita.domain.debt.Debt;
import com.domeni.kapita.domain.debt.DebtRepository;
import com.domeni.kapita.repositories.DebtSpringRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DebtRepositoryImpl implements DebtRepository {
  private final DebtSpringRepository debtSpringRepository;

  @Override
  public Debt save(Debt value) {
    return debtSpringRepository.save(value);
  }
}
