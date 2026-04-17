package com.domeni.kapita.domain.subscriptionplan;

import javax.money.MonetaryAmount;
import lombok.Builder;

@Builder
public record SubscriptionPlanData(
    SubscriptionPlanStatus status,
    Integer durationValue,
    SubscriptionPlanDurationUnit durationUnit,
    MonetaryAmount price) {}
