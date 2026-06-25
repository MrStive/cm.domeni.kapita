package com.domeni.kapita.service.events.inbound;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.domeni.kapita.domain.cache.SubscriptionStatusCache;
import com.domeni.kapita.domain.payment.PaymentStatus;
import com.domeni.kapita.domain.subscriptionplan.Subscription;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionFetcher;
import com.domeni.kapita.domain.user.UserId;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlan;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanDurationUnit;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanFetcher;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanId;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionUpdater;
import com.domeni.kapita.kafka.inbound.InboundEventContext;
import com.domeni.kapita.service.events.model.PaymentStatusEventDTO;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentStatusInboundEventHandlerTest {

  @Mock private SubscriptionFetcher subscriptionFetcher;
  @Mock private SubscriptionPlanFetcher subscriptionPlanFetcher;
  @Mock private SubscriptionUpdater subscriptionUpdater;
  @Mock private SubscriptionStatusCache subscriptionStatusCache;

  private final Clock clock = Clock.fixed(Instant.parse("2026-06-25T10:00:00Z"), ZoneId.of("UTC"));
  private PaymentStatusInboundEventHandler paymentStatusInboundEventHandler;

  @BeforeEach
  void setUp() {
    paymentStatusInboundEventHandler =
        new PaymentStatusInboundEventHandler(
            subscriptionFetcher, subscriptionPlanFetcher, subscriptionUpdater, subscriptionStatusCache, clock);
  }

  private Subscription createTestSubscription() {
    Subscription subscription = new Subscription();
    subscription.setPlanId(new SubscriptionPlanId(UUID.randomUUID()));
    subscription.setUserId(new UserId(UUID.randomUUID()));
    return subscription;
  }

  @Test
  void handleSuccessShouldActivateSubscriptionTest() {
    UUID paymentId = UUID.randomUUID();
    Subscription subscription = createTestSubscription();

    SubscriptionPlan plan = new SubscriptionPlan();
    plan.setDurationValue(1);
    plan.setDurationUnit(SubscriptionPlanDurationUnit.MONTH);

    given(subscriptionFetcher.getByPaymentTransactionId(paymentId))
        .willReturn(Optional.of(subscription));
    given(subscriptionPlanFetcher.getById(subscription.getPlanId())).willReturn(Optional.of(plan));

    paymentStatusInboundEventHandler.handle(
        createEvent(paymentId, PaymentStatus.SUCCESS, null),
        new InboundEventContext("payment.status.changed", 0, 10L));

    then(subscriptionUpdater).should().activate(subscription, plan, clock);
    then(subscriptionStatusCache).should().evict(subscription.getUserId().toUUID());
  }

  @Test
  void handleFailedShouldCancelSubscriptionTest() {
    UUID paymentId = UUID.randomUUID();
    Subscription subscription = createTestSubscription();

    given(subscriptionFetcher.getByPaymentTransactionId(paymentId))
        .willReturn(Optional.of(subscription));

    paymentStatusInboundEventHandler.handle(
        createEvent(paymentId, PaymentStatus.FAILED, "Provider error"),
        new InboundEventContext("payment.status.changed", 0, 10L));

    then(subscriptionUpdater).should().cancel(subscription);
    then(subscriptionStatusCache).should().evict(subscription.getUserId().toUUID());
  }

  @Test
  void handleCancelledShouldCancelSubscriptionTest() {
    UUID paymentId = UUID.randomUUID();
    Subscription subscription = createTestSubscription();

    given(subscriptionFetcher.getByPaymentTransactionId(paymentId))
        .willReturn(Optional.of(subscription));

    paymentStatusInboundEventHandler.handle(
        createEvent(paymentId, PaymentStatus.CANCELLED, null),
        new InboundEventContext("payment.status.changed", 0, 10L));

    then(subscriptionUpdater).should().cancel(subscription);
    then(subscriptionStatusCache).should().evict(subscription.getUserId().toUUID());
  }

  @Test
  void handleExpiredShouldCancelSubscriptionTest() {
    UUID paymentId = UUID.randomUUID();
    Subscription subscription = createTestSubscription();

    given(subscriptionFetcher.getByPaymentTransactionId(paymentId))
        .willReturn(Optional.of(subscription));

    paymentStatusInboundEventHandler.handle(
        createEvent(paymentId, PaymentStatus.EXPIRED, "Payment expired"),
        new InboundEventContext("payment.status.changed", 0, 10L));

    then(subscriptionUpdater).should().cancel(subscription);
    then(subscriptionStatusCache).should().evict(subscription.getUserId().toUUID());
  }

  @Test
  void handlePendingShouldNotModifySubscriptionTest() {
    UUID paymentId = UUID.randomUUID();
    Subscription subscription = createTestSubscription();

    given(subscriptionFetcher.getByPaymentTransactionId(paymentId))
        .willReturn(Optional.of(subscription));

    paymentStatusInboundEventHandler.handle(
        createEvent(paymentId, PaymentStatus.PENDING, null),
        new InboundEventContext("payment.status.changed", 0, 10L));

    then(subscriptionUpdater).should(never()).activate(subscription, null, clock);
    then(subscriptionUpdater).should(never()).cancel(subscription);
  }

  private PaymentStatusEventDTO createEvent(
      UUID paymentId, PaymentStatus status, @Nullable String failureReason) {
    return new PaymentStatusEventDTO(
        paymentId.toString(),
        "ext-ref-1",
        "SUBSCRIPTION_PAYMENT",
        "user-1",
        status,
        BigDecimal.valueOf(1000),
        "XAF",
        "MONETBIL",
        UUID.randomUUID().toString(),
        null,
        failureReason,
        Instant.now(clock));
  }
}
