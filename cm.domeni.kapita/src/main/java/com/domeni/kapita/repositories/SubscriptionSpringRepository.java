package com.domeni.kapita.repositories;

import com.domeni.kapita.domain.subscriptionplan.Subscription;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionId;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanId;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionStatus;
import com.domeni.kapita.domain.user.UserId;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionSpringRepository extends JpaRepository<Subscription, SubscriptionId> {
  Optional<Subscription> findByPaymentTransactionId(UUID paymentTransactionId);

  Optional<Subscription> findByUserIdAndPlanIdAndStatus(
      UserId userId, SubscriptionPlanId planId, SubscriptionStatus status);
}
