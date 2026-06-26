package com.domeni.kapita.service;

import com.domeni.kapita.config.KapitaSubscriptionProperties;
import com.domeni.kapita.domain.cache.SubscriptionStatusCache;
import com.domeni.kapita.domain.cache.SubscriptionStatusCacheValue;
import com.domeni.kapita.domain.exception.SubscriptionAccessException;
import com.domeni.kapita.domain.subscriptionplan.Subscription;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionData;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionFactory;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionFetcher;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionStatus;
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
  private final SubscriptionFactory subscriptionFactory;
  private final SubscriptionStatusCache cache;
  private final KapitaSubscriptionProperties properties;
  private final Clock clock;

  public void validateWriteAccess(UUID userId) {
    var cached = cache.get(userId);
    if (cached.isPresent()) {
      evaluate(cached.get(), userId);
      return;
    }

    var subscription = subscriptionFetcher.getByUserId(new UserId(userId));
    if (subscription.isEmpty()) {
      var trial = createTrialSubscription(userId);
      var cacheValue =
          new SubscriptionStatusCacheValue(
              SubscriptionStatus.TRIAL, trial.getTrialEndDate(), null);
      cache.set(userId, cacheValue, Duration.ofSeconds(properties.getCache().getDefaultTtlSeconds()));
      throw new SubscriptionAccessException(
          "Aucun abonnement actif. Profitez de votre période d'essai de "
              + properties.getTrialPeriodDays()
              + " jours en accès lecture seule.");
    }

    var sub = subscription.get();
    var value =
        new SubscriptionStatusCacheValue(
            sub.getStatus(), sub.getTrialEndDate(), sub.getEndDate());
    cache.set(userId, value, Duration.ofSeconds(properties.getCache().getDefaultTtlSeconds()));
    evaluate(value, userId);
  }

  private void evaluate(SubscriptionStatusCacheValue value, UUID userId) {
    var now = LocalDateTime.now(clock);
    switch (value.status()) {
      case TRIAL -> {
        if (value.trialEndDate() != null && now.isAfter(value.trialEndDate())) {
          throw new SubscriptionAccessException("Votre période d'essai a expiré.");
        }
        throw new SubscriptionAccessException(
            "Profitez de votre période d'essai de "
                + properties.getTrialPeriodDays()
                + " jours en accès lecture seule.");
      }
      case ACTIVE -> {
        if (value.endDate() != null && now.isAfter(value.endDate())) {
          throw new SubscriptionAccessException("Votre abonnement a expiré.");
        }
      }
      case EXPIRED, CANCELLED ->
          throw new SubscriptionAccessException("Votre abonnement n'est plus actif.");
      case PENDING ->
          throw new SubscriptionAccessException("Votre abonnement est en attente de validation.");
    }
  }

  private Subscription createTrialSubscription(UUID userId) {
    var now = LocalDateTime.now(clock);
    var trialEndDate = now.plusDays(properties.getTrialPeriodDays());
    var data =
        SubscriptionData.builder()
            .userId(new UserId(userId))
            .planId(null)
            .status(SubscriptionStatus.TRIAL)
            .trialEndDate(trialEndDate)
            .build();
    return subscriptionFactory.create(data);
  }
}
