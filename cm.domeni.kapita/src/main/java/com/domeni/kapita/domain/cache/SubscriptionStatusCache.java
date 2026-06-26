package com.domeni.kapita.domain.cache;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionStatusCache {
  Optional<SubscriptionStatusCacheValue> get(UUID userId);
  void set(UUID userId, SubscriptionStatusCacheValue value, Duration ttl);
  void evict(UUID userId);
}
