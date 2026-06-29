package com.domeni.kapita.domain.subscriptionplan;

import java.time.Clock;

public interface SubscriptionUpdater {

  Subscription activate(Subscription subscription, SubscriptionPlan plan, Clock clock);

  Subscription cancel(Subscription subscription);

  Subscription expire(Subscription subscription);
}
