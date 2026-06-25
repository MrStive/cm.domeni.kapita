package com.domeni.kapita.domain.cache;

import com.domeni.kapita.domain.subscriptionplan.SubscriptionStatus;
import java.time.LocalDateTime;
import org.springframework.lang.Nullable;

public record SubscriptionStatusCacheValue(
    SubscriptionStatus status,
    @Nullable LocalDateTime trialEndDate,
    @Nullable LocalDateTime endDate) {}
