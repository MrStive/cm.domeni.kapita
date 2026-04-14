package com.domeni.kapita.repositories.impl;

import com.domeni.kapita.domain.debt.Debt;
import com.domeni.kapita.domain.debt.DebtId;
import com.domeni.kapita.domain.debt.DebtPage;
import com.domeni.kapita.domain.debt.DebtRepository;
import com.domeni.kapita.domain.debt.DebtType;
import com.domeni.kapita.domain.user.UserId;
import com.domeni.kapita.repositories.DebtSpringRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@RequiredArgsConstructor
public class DebtRepositoryImpl implements DebtRepository {
  private final DebtSpringRepository debtSpringRepository;

  @Override
  public Debt save(Debt value) {
    return debtSpringRepository.save(value);
  }

  @Override
  public Optional<Debt> findByIdAndUserId(DebtId debtId, UserId userId) {
    return debtSpringRepository.findByIdAndUserId(debtId, userId);
  }

  @Override
  public DebtPage findAllByUserId(UserId userId, int pageNumber, int pageSize) {
    Page<Debt> page =
        debtSpringRepository.findAllByUserId(
            userId,
            PageRequest.of(
                pageNumber, pageSize, Sort.by(Sort.Direction.DESC, Debt.Fields.createdAt)));

    return toPage(page);
  }

  @Override
  public DebtPage findAllByUserIdAndType(
      UserId userId, DebtType type, int pageNumber, int pageSize) {
    Page<Debt> page =
        debtSpringRepository.findAllByUserIdAndType(
            userId,
            type,
            PageRequest.of(
                pageNumber, pageSize, Sort.by(Sort.Direction.DESC, Debt.Fields.createdAt)));

    return toPage(page);
  }

  private DebtPage toPage(Page<Debt> page) {
    return new DebtPage(
        page.getContent(),
        page.getNumber(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages());
  }
}
