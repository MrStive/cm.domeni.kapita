package com.domeni.kapita.domain.subscriptionplan.impl;

import com.domeni.kapita.domain.subscriptionplan.Subscription;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionData;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionFactory;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionId;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionRepository;
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
        Subscription.builder()
            .id(SubscriptionId.generate())
            .userId(data.userId())
            .planId(data.planId())
            .status(data.status())
            .paymentTransactionId(data.paymentTransactionId())
            .createdAt(LocalDateTime.now(clock))
            .build();
    return subscriptionRepository.save(subscription);
  }
}
