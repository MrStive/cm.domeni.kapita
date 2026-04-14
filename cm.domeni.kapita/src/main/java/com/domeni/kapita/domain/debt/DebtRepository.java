package com.domeni.kapita.domain.debt;

import com.domeni.kapita.domain.user.UserId;
import java.util.Optional;

public interface DebtRepository {

  Debt save(Debt value);

  Optional<Debt> findByIdAndUserId(DebtId debtId, UserId userId);

  DebtPage findAllByUserId(UserId userId, int pageNumber, int pageSize);

  DebtPage findAllByUserIdAndType(UserId userId, DebtType type, int pageNumber, int pageSize);
}
