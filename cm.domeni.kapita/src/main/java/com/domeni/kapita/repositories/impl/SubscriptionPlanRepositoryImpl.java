package com.domeni.kapita.repositories.impl;

import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlan;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanId;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanRepository;
import com.domeni.kapita.repositories.SubscriptionPlanSpringRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SubscriptionPlanRepositoryImpl implements SubscriptionPlanRepository {

  private final SubscriptionPlanSpringRepository subscriptionPlanSpringRepository;

  @Override
  public SubscriptionPlan save(SubscriptionPlan value) {
    return subscriptionPlanSpringRepository.save(value);
  }

  @Override
  public Optional<SubscriptionPlan> findById(SubscriptionPlanId id) {
    return subscriptionPlanSpringRepository.findById(id);
  }
}
