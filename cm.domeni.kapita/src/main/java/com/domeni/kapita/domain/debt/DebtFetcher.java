package com.domeni.kapita.domain.debt;

import com.domeni.kapita.domain.user.UserId;

public interface DebtFetcher {

  DebtPage getByType(DebtType type, Integer pageNumber, Integer pageSize, UserId currentUserId);
}
