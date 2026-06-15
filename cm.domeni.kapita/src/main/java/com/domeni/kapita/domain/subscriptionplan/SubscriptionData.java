package com.domeni.kapita.domain.subscriptionplan;

import com.domeni.kapita.domain.user.UserId;
import java.util.UUID;
import lombok.Builder;

@Builder
public record SubscriptionData(
    UserId userId,
    SubscriptionPlanId planId,
    SubscriptionStatus status,
    UUID paymentTransactionId) {}
