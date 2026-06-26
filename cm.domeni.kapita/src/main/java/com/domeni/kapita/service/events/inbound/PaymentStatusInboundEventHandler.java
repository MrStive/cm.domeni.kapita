package com.domeni.kapita.service.events.inbound;

import com.domeni.kapita.domain.cache.SubscriptionStatusCache;
import com.domeni.kapita.domain.subscriptionplan.Subscription;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionFetcher;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlan;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanFetcher;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionUpdater;
import com.domeni.kapita.kafka.inbound.InboundEventContext;
import com.domeni.kapita.kafka.inbound.InboundEventHandler;
import com.domeni.kapita.service.events.model.PaymentStatusEventDTO;
import java.time.Clock;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentStatusInboundEventHandler
    implements InboundEventHandler<PaymentStatusEventDTO> {

  private final SubscriptionFetcher subscriptionFetcher;
  private final SubscriptionPlanFetcher subscriptionPlanFetcher;
  private final SubscriptionUpdater subscriptionUpdater;
  private final SubscriptionStatusCache cache;
  private final Clock clock;

  @Override
  public void handle(PaymentStatusEventDTO payload, InboundEventContext context) {
    log.info("Handling payment status event: {}", payload);

    Subscription subscription =
        subscriptionFetcher
            .getByPaymentTransactionId(UUID.fromString(payload.paymentId()))
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "Subscription not found for paymentId: " + payload.paymentId()));

    switch (payload.status()) {
      case SUCCESS -> {
        SubscriptionPlan plan =
            subscriptionPlanFetcher
                .getById(subscription.getPlanId())
                .orElseThrow(
                    () ->
                        new IllegalStateException(
                            "Plan not found for subscription: " + subscription.getId()));
        subscriptionUpdater.activate(subscription, plan, clock);
        cache.evict(subscription.getUserId().toUUID());
        log.info("Subscription activated: {}", subscription.getId());
      }
      case FAILED, CANCELLED, EXPIRED -> {
        log.warn(
            "Payment {} for subscription {}: {}",
            payload.status(),
            subscription.getId(),
            payload.failureReason());
        subscriptionUpdater.cancel(subscription);
        cache.evict(subscription.getUserId().toUUID());
      }
      case PENDING -> log.info("Payment still pending for subscription: {}", subscription.getId());
    }
  }

  @Override
  public Class<PaymentStatusEventDTO> payloadType() {
    return PaymentStatusEventDTO.class;
  }

  @Override
  public String eventType() {
    return "PAYMENT_STATUS_CHANGED";
  }
}
