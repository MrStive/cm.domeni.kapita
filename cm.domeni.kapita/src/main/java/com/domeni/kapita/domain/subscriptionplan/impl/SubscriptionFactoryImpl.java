package com.domeni.kapita.domain.subscriptionplan.impl;

import com.domeni.kapita.domain.subscriptionplan.Subscription;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionData;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionFactory;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionId;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionRepository;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionStatus;
import com.domeni.kapita.domain.user.UserId;
import java.time.Clock;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SubscriptionFactoryImpl implements SubscriptionFactory {

  private final SubscriptionRepository subscriptionRepository;
  private final Clock clock;

  @Override
  public Subscription create(SubscriptionData data) {
    if (data == null) {
      throw new IllegalArgumentException("subscription data is required");
    }
    Subscription subscription =
        new Subscription(
            SubscriptionId.generate(),
            data.userId(),
            data.planId(),
            data.status(),
            data.paymentTransactionId(),
            null,
            null,
            data.trialEndDate(),
            LocalDateTime.now(clock));
    return subscriptionRepository.save(subscription);
  }

  @Override
  public Subscription createTrial(UserId userId, int trialPeriodDays) {
    if (subscriptionRepository.hasUserEverHadTrial(userId)) {
      throw new IllegalStateException(
          "L'utilisateur " + userId.getValue() + " a déjà bénéficié d'un essai.");
    }
    var now = LocalDateTime.now(clock);
    var trialEndDate = now.plusDays(trialPeriodDays);
    var data =
        SubscriptionData.builder()
            .userId(userId)
            .planId(null)
            .status(SubscriptionStatus.TRIAL)
            .trialEndDate(trialEndDate)
            .build();
    return create(data);
  }
}
