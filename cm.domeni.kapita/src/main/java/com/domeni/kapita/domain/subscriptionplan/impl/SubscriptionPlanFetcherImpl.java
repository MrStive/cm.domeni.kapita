package com.domeni.kapita.domain.subscriptionplan.impl;

import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlan;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanFetcher;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanId;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SubscriptionPlanFetcherImpl implements SubscriptionPlanFetcher {

  private final SubscriptionPlanRepository subscriptionPlanRepository;

  @Override
  public Optional<SubscriptionPlan> getById(SubscriptionPlanId id) {
    return subscriptionPlanRepository.findById(id);
  }
}
