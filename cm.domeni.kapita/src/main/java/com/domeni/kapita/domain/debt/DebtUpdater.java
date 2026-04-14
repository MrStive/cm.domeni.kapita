package com.domeni.kapita.domain.debt;

import com.domeni.kapita.domain.user.UserId;

public interface DebtUpdater {

  Debt settle(DebtId debtId, UserId currentUserId);
}
