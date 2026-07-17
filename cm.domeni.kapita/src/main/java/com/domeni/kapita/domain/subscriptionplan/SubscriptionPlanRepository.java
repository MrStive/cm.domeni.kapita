package com.domeni.kapita.domain.subscriptionplan;

import java.util.List;
import java.util.Optional;

public interface SubscriptionPlanRepository {

  SubscriptionPlan save(SubscriptionPlan value);

  Optional<SubscriptionPlan> findById(SubscriptionPlanId id);

  List<SubscriptionPlan> findAllByStatus(SubscriptionPlanStatus status);
}
