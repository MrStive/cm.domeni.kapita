package com.domeni.kapita.domain.subscriptionplan;

import java.util.Optional;

public interface SubscriptionPlanRepository {

  SubscriptionPlan save(SubscriptionPlan value);

  Optional<SubscriptionPlan> findById(SubscriptionPlanId id);
}
