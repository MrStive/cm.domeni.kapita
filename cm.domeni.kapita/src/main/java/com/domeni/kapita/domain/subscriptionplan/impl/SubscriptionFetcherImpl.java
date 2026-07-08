package com.domeni.kapita.domain.subscriptionplan.impl;

import com.domeni.kapita.domain.subscriptionplan.Subscription;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionFetcher;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionRepository;
import com.domeni.kapita.domain.user.UserId;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SubscriptionFetcherImpl implements SubscriptionFetcher {

  private final SubscriptionRepository subscriptionRepository;

  @Override
  public Optional<Subscription> getByPaymentTransactionId(UUID paymentTransactionId) {
    return subscriptionRepository.findByPaymentTransactionId(paymentTransactionId);
  }

  @Override
  public Optional<Subscription> getByUserId(UserId userId) {
    return subscriptionRepository.findByUserId(userId).stream()
        .min(
            Comparator.comparingInt((Subscription s) -> s.getStatus().sortOrder())
                .thenComparing(Comparator.comparing(Subscription::getCreatedAt).reversed()));
  }
}
