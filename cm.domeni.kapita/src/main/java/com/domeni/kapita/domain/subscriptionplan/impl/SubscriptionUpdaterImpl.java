package com.domeni.kapita.domain.subscriptionplan.impl;

import com.domeni.kapita.domain.subscriptionplan.Subscription;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlan;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionRepository;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionUpdater;
import java.time.Clock;
import java.time.LocalDateTime;

public class SubscriptionUpdaterImpl implements SubscriptionUpdater {

  private final SubscriptionRepository subscriptionRepository;
  private final Clock clock;

  public SubscriptionUpdaterImpl(SubscriptionRepository subscriptionRepository, Clock clock) {
    this.subscriptionRepository = subscriptionRepository;
    this.clock = clock;
  }

  @Override
  public Subscription activate(Subscription subscription, SubscriptionPlan plan, Clock clock) {
    var now = LocalDateTime.now(clock);
    LocalDateTime endDate =
        switch (plan.getDurationUnit()) {
          case WEEK -> now.plusWeeks(plan.getDurationValue());
          case MONTH -> now.plusMonths(plan.getDurationValue());
          case YEAR -> now.plusYears(plan.getDurationValue());
        };
    subscription.activate(now, endDate);
    return subscriptionRepository.save(subscription);
  }

  @Override
  public Subscription cancel(Subscription subscription) {
    subscription.cancel();
    return subscriptionRepository.save(subscription);
  }

  @Override
  public Subscription expire(Subscription subscription) {
    if (subscription.expire(clock)) {
      return subscriptionRepository.save(subscription);
    }
    return subscription;
  }
}
