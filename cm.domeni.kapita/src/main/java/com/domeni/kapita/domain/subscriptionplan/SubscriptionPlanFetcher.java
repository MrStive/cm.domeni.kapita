package com.domeni.kapita.domain.subscriptionplan;

import java.util.Optional;

public interface SubscriptionPlanFetcher {
  Optional<SubscriptionPlan> getById(SubscriptionPlanId id);
}
