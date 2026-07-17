package com.domeni.kapita.domain.subscriptionplan;

import java.util.List;
import java.util.Optional;

public interface SubscriptionPlanFetcher {
  Optional<SubscriptionPlan> getById(SubscriptionPlanId id);

  List<SubscriptionPlan> getAllActive();
}
