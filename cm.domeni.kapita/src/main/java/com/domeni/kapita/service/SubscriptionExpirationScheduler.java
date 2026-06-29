package com.domeni.kapita.service;

import com.domeni.kapita.domain.cache.SubscriptionStatusCache;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionRepository;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionUpdater;
import java.time.Clock;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(
    value = "kapita.subscription.scheduler.enabled",
    havingValue = "true",
    matchIfMissing = true)
public class SubscriptionExpirationScheduler {

  private final SubscriptionRepository subscriptionRepository;
  private final SubscriptionUpdater subscriptionUpdater;
  private final SubscriptionStatusCache cache;
  private final Clock clock;

  @Scheduled(cron = "${kapita.subscription.scheduler.cron}")
  public void expireSubscriptions() {
    log.info("Starting subscription expiration check...");
    var now = LocalDateTime.now(clock);
    var expiredSubscriptions = subscriptionRepository.findExpiredSubscriptions(now);

    if (expiredSubscriptions.isEmpty()) {
      log.info("No expired subscriptions found.");
      return;
    }

    log.info("Found {} expired subscriptions. Marking as EXPIRED...", expiredSubscriptions.size());

    for (var subscription : expiredSubscriptions) {
      try {
        subscriptionUpdater.expire(subscription);
        cache.evict(subscription.getUserId().toUUID());
        log.info(
            "Subscription {} for user {} marked as EXPIRED.",
            subscription.getId().toUUID(),
            subscription.getUserId().getValue());
      } catch (Exception e) {
        log.error(
            "Failed to expire subscription {} for user {}",
            subscription.getId().toUUID(),
            subscription.getUserId().getValue(),
            e);
      }
    }

    log.info("Subscription expiration check completed.");
  }
}
