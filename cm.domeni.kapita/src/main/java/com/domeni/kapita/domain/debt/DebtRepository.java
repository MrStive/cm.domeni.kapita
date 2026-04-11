package com.domeni.kapita.domain.debt;

import com.domeni.kapita.domain.user.UserId;

public interface DebtRepository {

  Debt save(Debt value);

  DebtPage findAllByUserIdAndType(UserId userId, DebtType type, int pageNumber, int pageSize);
}
