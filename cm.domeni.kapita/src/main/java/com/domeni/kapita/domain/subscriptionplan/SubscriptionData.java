package com.domeni.kapita.domain.subscriptionplan;

import com.domeni.kapita.domain.user.UserId;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import org.springframework.lang.Nullable;

@Builder
public record SubscriptionData(
    UserId userId,
    @Nullable SubscriptionPlanId planId,
    SubscriptionStatus status,
    UUID paymentTransactionId,
    @Nullable LocalDateTime trialEndDate) {}
