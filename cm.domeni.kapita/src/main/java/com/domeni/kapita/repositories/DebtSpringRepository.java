package com.domeni.kapita.repositories;

import com.domeni.kapita.domain.debt.Debt;
import com.domeni.kapita.domain.debt.DebtId;
import com.domeni.kapita.domain.debt.DebtType;
import com.domeni.kapita.domain.user.UserId;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DebtSpringRepository extends JpaRepository<Debt, DebtId> {

  Optional<Debt> findByIdAndUserId(DebtId debtId, UserId userId);

  Page<Debt> findAllByUserId(UserId userId, Pageable pageable);

  Page<Debt> findAllByUserIdAndType(UserId userId, DebtType type, Pageable pageable);
}
