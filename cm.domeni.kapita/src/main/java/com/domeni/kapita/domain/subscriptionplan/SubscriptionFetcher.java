package com.domeni.kapita.domain.subscriptionplan;

import com.domeni.kapita.domain.user.UserId;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionFetcher {
  Optional<Subscription> getByPaymentTransactionId(UUID paymentTransactionId);

  Optional<Subscription> getByUserId(UserId userId);
}
