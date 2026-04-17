package com.domeni.kapita.domain.subscriptionplan.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verifyNoInteractions;

import com.domeni.kapita.domain.exception.InvalidSubscriptionPlanPayloadException;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlan;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanData;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanDurationUnit;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanRepository;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanStatus;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SubscriptionPlanFactoryImplTest {

  @Mock private SubscriptionPlanRepository subscriptionPlanRepository;

  @Test
  void createShouldBuildAndPersistSubscriptionPlanFromSubscriptionPlanDataTest() {
    Clock fixedClock = Clock.fixed(Instant.parse("2026-04-10T10:15:30Z"), ZoneOffset.UTC);
    SubscriptionPlanFactoryImpl subscriptionPlanFactory =
        new SubscriptionPlanFactoryImpl(subscriptionPlanRepository, fixedClock);
    SubscriptionPlanData input =
        SubscriptionPlanData.builder()
            .status(SubscriptionPlanStatus.ACTIVE)
            .durationValue(6)
            .durationUnit(SubscriptionPlanDurationUnit.MONTH)
            .price(Money.of(new BigDecimal("12500.00"), "XAF"))
            .build();
    SubscriptionPlan persistedSubscriptionPlan = new SubscriptionPlan();
    given(subscriptionPlanRepository.save(any(SubscriptionPlan.class)))
        .willReturn(persistedSubscriptionPlan);

    SubscriptionPlan result = subscriptionPlanFactory.create(input);

    assertThat(result).isSameAs(persistedSubscriptionPlan);

    ArgumentCaptor<SubscriptionPlan> subscriptionPlanCaptor =
        ArgumentCaptor.forClass(SubscriptionPlan.class);
    then(subscriptionPlanRepository).should().save(subscriptionPlanCaptor.capture());

    SubscriptionPlan subscriptionPlanToSave = subscriptionPlanCaptor.getValue();
    assertThat(subscriptionPlanToSave.getId()).isNotNull();
    assertThat(subscriptionPlanToSave.getId().toUUID()).isNotNull();
    assertThat(subscriptionPlanToSave.getStatus()).isEqualTo(SubscriptionPlanStatus.ACTIVE);
    assertThat(subscriptionPlanToSave.getDurationValue()).isEqualTo(6);
    assertThat(subscriptionPlanToSave.getDurationUnit())
        .isEqualTo(SubscriptionPlanDurationUnit.MONTH);
    assertThat(subscriptionPlanToSave.getPrice().getCurrency().getCurrencyCode()).isEqualTo("XAF");
    assertThat(subscriptionPlanToSave.getPrice().getNumber().numberValue(BigDecimal.class))
        .isEqualByComparingTo("12500.00");
    assertThat(subscriptionPlanToSave.getCreatedAt())
        .isEqualTo(LocalDateTime.of(2026, 4, 10, 10, 15, 30));
  }

  @Test
  void createWhenDurationIsInvalidShouldThrowInvalidSubscriptionPlanPayloadExceptionTest() {
    SubscriptionPlanFactoryImpl subscriptionPlanFactory =
        new SubscriptionPlanFactoryImpl(subscriptionPlanRepository, Clock.systemUTC());
    SubscriptionPlanData input =
        SubscriptionPlanData.builder()
            .status(SubscriptionPlanStatus.ACTIVE)
            .durationValue(0)
            .durationUnit(SubscriptionPlanDurationUnit.MONTH)
            .price(Money.of(new BigDecimal("12500.00"), "XAF"))
            .build();

    assertThatThrownBy(() -> subscriptionPlanFactory.create(input))
        .isInstanceOf(InvalidSubscriptionPlanPayloadException.class)
        .hasMessage("subscription plan duration must be greater than zero");

    verifyNoInteractions(subscriptionPlanRepository);
  }

  @Test
  void createWhenPriceIsMissingShouldThrowInvalidSubscriptionPlanPayloadExceptionTest() {
    SubscriptionPlanFactoryImpl subscriptionPlanFactory =
        new SubscriptionPlanFactoryImpl(subscriptionPlanRepository, Clock.systemUTC());
    SubscriptionPlanData input =
        SubscriptionPlanData.builder()
            .status(SubscriptionPlanStatus.ACTIVE)
            .durationValue(2)
            .durationUnit(SubscriptionPlanDurationUnit.YEAR)
            .price(null)
            .build();

    assertThatThrownBy(() -> subscriptionPlanFactory.create(input))
        .isInstanceOf(InvalidSubscriptionPlanPayloadException.class)
        .hasMessage("subscription plan price must be greater than zero");

    verifyNoInteractions(subscriptionPlanRepository);
  }
}
