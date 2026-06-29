package com.domeni.kapita.service;

import com.domeni.kapita.config.KapitaSubscriptionProperties;
import com.domeni.kapita.domain.cache.SubscriptionStatusCache;
import com.domeni.kapita.domain.cache.SubscriptionStatusCacheValue;
import com.domeni.kapita.domain.exception.SubscriptionAccessException;
import com.domeni.kapita.domain.subscriptionplan.Subscription;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionFetcher;
import com.domeni.kapita.domain.user.UserId;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriptionStatusService {

  private final SubscriptionFetcher subscriptionFetcher;
  private final SubscriptionStatusCache cache;
  private final KapitaSubscriptionProperties properties;
  private final Clock clock;

  public void validateWriteAccess(UUID userId) {
    var cached = cache.get(userId);
    if (cached.isPresent()) {
      var v = cached.get();
      Subscription.ensureWriteAccess(
          v.status(), v.trialEndDate(), v.endDate(), LocalDateTime.now(clock));
      return;
    }

    var subscription = subscriptionFetcher.getByUserId(new UserId(userId));
    if (subscription.isEmpty()) {
      throw new SubscriptionAccessException("Aucun abonnement actif.");
    }

    var sub = subscription.get();
    var value =
        new SubscriptionStatusCacheValue(sub.getStatus(), sub.getTrialEndDate(), sub.getEndDate());
    cache.set(userId, value, Duration.ofSeconds(properties.getCache().getDefaultTtlSeconds()));
    sub.ensureWriteAccess(LocalDateTime.now(clock));
  }
}
