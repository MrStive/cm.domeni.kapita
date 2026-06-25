package com.domeni.kapita.infrastructure.impl;

import com.domeni.kapita.domain.cache.SubscriptionStatusCache;
import com.domeni.kapita.domain.cache.SubscriptionStatusCacheValue;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

@RequiredArgsConstructor
public class RedisSubscriptionStatusCache implements SubscriptionStatusCache {

  private static final String KEY_PREFIX = "kapita:subscription:status";

  private final RedisTemplate<Object, Object> redisTemplate;
  private final Duration defaultTtl;

  @Override
  public Optional<SubscriptionStatusCacheValue> get(UUID userId) {
    var value = redisTemplate.opsForValue().get(buildKey(userId));
    if (value instanceof SubscriptionStatusCacheValue cacheValue) {
      return Optional.of(cacheValue);
    }
    return Optional.empty();
  }

  @Override
  public void set(UUID userId, SubscriptionStatusCacheValue value, Duration ttl) {
    redisTemplate.opsForValue().set(buildKey(userId), value, ttl);
  }

  @Override
  public void evict(UUID userId) {
    redisTemplate.delete(buildKey(userId));
  }

  private String buildKey(UUID userId) {
    return KEY_PREFIX + ":" + userId;
  }
}
