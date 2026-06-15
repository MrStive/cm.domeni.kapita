package com.domeni.kapita.domain.subscriptionplan;

import com.domeni.kapita.domain.user.UserId;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository {
  Subscription save(Subscription subscription);

  Optional<Subscription> findByPaymentTransactionId(UUID paymentTransactionId);

  Optional<Subscription> findByUserIdAndPlanIdAndStatus(
      UserId userId, SubscriptionPlanId planId, SubscriptionStatus status);
}
