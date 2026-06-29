package com.domeni.kapita.domain.subscriptionplan.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.domeni.kapita.domain.subscriptionplan.Subscription;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlan;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanDurationUnit;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionRepository;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SubscriptionUpdaterImplTest {

  @Mock private SubscriptionRepository subscriptionRepository;

  private final Clock clock = Clock.fixed(Instant.parse("2026-06-25T10:00:00Z"), ZoneId.of("UTC"));

  @Test
  void activateShouldSetActiveStatusAndDatesTest() {
    SubscriptionUpdaterImpl updater = new SubscriptionUpdaterImpl(subscriptionRepository, clock);
    Subscription subscription =
        new Subscription(
            null, null, null, SubscriptionStatus.PENDING, null, null, null, null, null);

    SubscriptionPlan plan = new SubscriptionPlan();
    plan.setDurationValue(3);
    plan.setDurationUnit(SubscriptionPlanDurationUnit.MONTH);

    given(subscriptionRepository.save(subscription)).willReturn(subscription);

    Subscription result = updater.activate(subscription, plan, clock);

    assertThat(result).isSameAs(subscription);
    assertThat(subscription.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
    assertThat(subscription.getStartDate()).isEqualTo(LocalDateTime.now(clock));
    assertThat(subscription.getEndDate()).isEqualTo(subscription.getStartDate().plusMonths(3));
    then(subscriptionRepository).should().save(subscription);
  }

  @Test
  void cancelShouldSetCancelledStatusTest() {
    SubscriptionUpdaterImpl updater = new SubscriptionUpdaterImpl(subscriptionRepository, clock);
    Subscription subscription = new Subscription();

    given(subscriptionRepository.save(subscription)).willReturn(subscription);

    Subscription result = updater.cancel(subscription);

    assertThat(result).isSameAs(subscription);
    assertThat(subscription.getStatus()).isEqualTo(SubscriptionStatus.CANCELLED);
    then(subscriptionRepository).should().save(subscription);
  }

  @Test
  void expireShouldSetExpiredStatusAndSaveTest() {
    SubscriptionUpdaterImpl updater = new SubscriptionUpdaterImpl(subscriptionRepository, clock);
    Subscription subscription =
        new Subscription(
            null,
            null,
            null,
            SubscriptionStatus.ACTIVE,
            null,
            null,
            LocalDateTime.now(clock).minusDays(1),
            null,
            null);

    given(subscriptionRepository.save(subscription)).willReturn(subscription);

    Subscription result = updater.expire(subscription);

    assertThat(result).isSameAs(subscription);
    assertThat(subscription.getStatus()).isEqualTo(SubscriptionStatus.EXPIRED);
    then(subscriptionRepository).should().save(subscription);
  }

  @Test
  void expireShouldBeIdempotentWhenAlreadyExpiredTest() {
    SubscriptionUpdaterImpl updater = new SubscriptionUpdaterImpl(subscriptionRepository, clock);
    Subscription subscription =
        new Subscription(
            null, null, null, SubscriptionStatus.EXPIRED, null, null, null, null, null);

    Subscription result = updater.expire(subscription);

    assertThat(result).isSameAs(subscription);
    assertThat(subscription.getStatus()).isEqualTo(SubscriptionStatus.EXPIRED);
    then(subscriptionRepository).should(never()).save(subscription);
  }
}
