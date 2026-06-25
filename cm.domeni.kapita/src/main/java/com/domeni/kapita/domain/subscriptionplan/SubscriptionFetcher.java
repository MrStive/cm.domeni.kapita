package com.domeni.kapita.domain.subscriptionplan;

import java.util.Optional;
import java.util.UUID;

public interface SubscriptionFetcher {
  Optional<Subscription> getByPaymentTransactionId(UUID paymentTransactionId);
}
