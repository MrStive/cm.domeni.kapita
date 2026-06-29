package com.domeni.kapita.domain.subscriptionplan;

import com.domeni.kapita.domain.user.UserId;

public interface SubscriptionFactory {
  Subscription create(SubscriptionData data);

  Subscription createTrial(UserId userId, int trialPeriodDays);
}
