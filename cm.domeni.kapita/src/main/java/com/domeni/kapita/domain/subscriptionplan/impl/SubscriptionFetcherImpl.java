package com.domeni.kapita.domain.subscriptionplan.impl;

import com.domeni.kapita.domain.subscriptionplan.Subscription;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionFetcher;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionRepository;
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
}
