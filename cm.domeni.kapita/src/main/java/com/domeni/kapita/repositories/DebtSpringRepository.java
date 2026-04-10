package com.domeni.kapita.repositories;

import com.domeni.kapita.domain.debt.Debt;
import com.domeni.kapita.domain.debt.DebtId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DebtSpringRepository extends JpaRepository<Debt, DebtId> {}
