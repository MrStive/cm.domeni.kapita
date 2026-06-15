package com.domeni.kapita.domain.subscriptionplan.impl;

import com.domeni.kapita.domain.subscriptionplan.Subscription;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlan;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionRepository;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionStatus;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionUpdater;
import java.time.Clock;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SubscriptionUpdaterImpl implements SubscriptionUpdater {

  private final SubscriptionRepository subscriptionRepository;

  @Override
  public Subscription activate(Subscription subscription, SubscriptionPlan plan, Clock clock) {
    subscription.setStatus(SubscriptionStatus.ACTIVE);
    subscription.setStartDate(LocalDateTime.now(clock));

    LocalDateTime endDate =
        switch (plan.getDurationUnit()) {
          case WEEK -> subscription.getStartDate().plusWeeks(plan.getDurationValue());
          case MONTH -> subscription.getStartDate().plusMonths(plan.getDurationValue());
          case YEAR -> subscription.getStartDate().plusYears(plan.getDurationValue());
        };
    subscription.setEndDate(endDate);

    return subscriptionRepository.save(subscription);
  }

  @Override
  public Subscription cancel(Subscription subscription) {
    subscription.setStatus(SubscriptionStatus.CANCELLED);
    return subscriptionRepository.save(subscription);
  }
}
