package com.domeni.kapita.domain.subscriptionplan.impl;

import com.domeni.kapita.domain.exception.InvalidSubscriptionPlanPayloadException;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlan;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanData;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanFactory;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanId;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanRepository;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.money.MonetaryAmount;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SubscriptionPlanFactoryImpl implements SubscriptionPlanFactory {
  private final SubscriptionPlanRepository subscriptionPlanRepository;
  private final Clock clock;

  @Override
  public SubscriptionPlan create(SubscriptionPlanData subscriptionPlanData) {
    SubscriptionPlanData normalizedData = normalizeAndValidate(subscriptionPlanData);

    return subscriptionPlanRepository.save(
        SubscriptionPlan.builder()
            .id(new SubscriptionPlanId(UUID.randomUUID()))
            .status(normalizedData.status())
            .durationValue(normalizedData.durationValue())
            .durationUnit(normalizedData.durationUnit())
            .price(normalizedData.price())
            .createdAt(LocalDateTime.now(clock))
            .build());
  }

  private SubscriptionPlanData normalizeAndValidate(SubscriptionPlanData data) {
    if (data == null) {
      throw new InvalidSubscriptionPlanPayloadException("subscription plan payload is required");
    }
    if (data.status() == null) {
      throw new InvalidSubscriptionPlanPayloadException("subscription plan status is required");
    }
    if (data.durationValue() == null || data.durationValue() < 1) {
      throw new InvalidSubscriptionPlanPayloadException(
          "subscription plan duration must be greater than zero");
    }
    if (data.durationUnit() == null) {
      throw new InvalidSubscriptionPlanPayloadException(
          "subscription plan duration unit is required");
    }
    if (!isPositiveAmount(data.price())) {
      throw new InvalidSubscriptionPlanPayloadException(
          "subscription plan price must be greater than zero");
    }
    return data;
  }

  private boolean isPositiveAmount(MonetaryAmount amount) {
    return amount != null
        && amount.getNumber().numberValue(BigDecimal.class).compareTo(BigDecimal.ZERO) > 0;
  }
}
