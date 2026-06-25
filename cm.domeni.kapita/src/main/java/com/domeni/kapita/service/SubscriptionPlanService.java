package com.domeni.kapita.service;

import com.domeni.kapita.domain.exception.PaymentInitiationException;
import com.domeni.kapita.domain.exception.SubscriptionPlanNotFoundException;
import com.domeni.kapita.domain.payment.PaymentPort;
import com.domeni.kapita.domain.payment.PaymentProvider;
import com.domeni.kapita.domain.payment.PaymentRequest;
import com.domeni.kapita.domain.payment.PaymentResponse;
import com.domeni.kapita.domain.subscriptionplan.Subscription;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionData;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionFactory;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlan;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanData;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanFactory;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanFetcher;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanId;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionRepository;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionStatus;
import com.domeni.kapita.domain.user.UserId;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionPlanService {
  private final SubscriptionPlanFactory subscriptionPlanFactory;
  private final SubscriptionPlanFetcher subscriptionPlanFetcher;
  private final SubscriptionFactory subscriptionFactory;
  private final SubscriptionRepository subscriptionRepository;
  private final PaymentPort paymentPort;

  @Transactional
  public UUID createSubscriptionPlan(SubscriptionPlanData data) {
    SubscriptionPlan createdSubscriptionPlan = subscriptionPlanFactory.create(data);
    if (createdSubscriptionPlan.getId() == null) {
      throw new IllegalStateException("created subscription plan has no identifier");
    }
    return createdSubscriptionPlan.getId().toUUID();
  }

  @Transactional
  public PaymentResponse subscribeToPlan(UUID planId, UserId userId, String phoneNumber) {
    SubscriptionPlanId subscriptionPlanId = new SubscriptionPlanId(planId);

    Optional<Subscription> existingPending =
        subscriptionRepository.findByUserIdAndPlanIdAndStatus(
            userId, subscriptionPlanId, SubscriptionStatus.PENDING);

    if (existingPending.isPresent()) {
      Subscription sub = existingPending.get();
      if (sub.getPaymentTransactionId() != null) {
        log.info(
            "Found existing pending subscription {} for user {}, returning previous transaction",
            sub.getId().toUUID(),
            userId.getValue());
        return PaymentResponse.builder()
            .paymentId(sub.getPaymentTransactionId().toString())
            .externalReference(sub.getId().toUUID().toString())
            .status("PENDING")
            .paymentUrl(null)
            .build();
      }
    }

    SubscriptionPlan plan =
        subscriptionPlanFetcher
            .getById(subscriptionPlanId)
            .orElseThrow(
                () ->
                    new SubscriptionPlanNotFoundException(
                        "Subscription plan not found: " + planId));

    Subscription pendingSubscription =
        subscriptionFactory.create(
            SubscriptionData.builder()
                .userId(userId)
                .planId(plan.getId())
                .status(SubscriptionStatus.PENDING)
                .paymentTransactionId(null)
                .build());

    log.info(
        "Created pending subscription {} for plan {} and user {}",
        pendingSubscription.getId().toUUID(),
        planId,
        userId.getValue());

    try {
      PaymentResponse paymentResponse =
          paymentPort.initiatePayment(
              PaymentRequest.builder()
                  .amount(plan.getPrice().getNumber().numberValue(java.math.BigDecimal.class))
                  .currency(plan.getPrice().getCurrency().getCurrencyCode())
                  .description("Subscription " + pendingSubscription.getId().toUUID())
                  .phoneNumber(phoneNumber)
                  .provider(PaymentProvider.MONETBIL)
                  .externalReference(pendingSubscription.getId().toUUID().toString())
                  .purpose("SUBSCRIPTION_PAYMENT")
                  .idempotencyKey(pendingSubscription.getId().toUUID().toString())
                  .build());

      pendingSubscription.setPaymentTransactionId(UUID.fromString(paymentResponse.paymentId()));
      subscriptionRepository.save(pendingSubscription);

      log.info(
          "Payment initiated for subscription {}, paymentId={}",
          pendingSubscription.getId().toUUID(),
          paymentResponse.paymentId());

      return paymentResponse;

    } catch (Exception e) {
      log.error(
          "Payment initiation failed for subscription {}, marking as CANCELLED",
          pendingSubscription.getId().toUUID(),
          e);
      pendingSubscription.setStatus(SubscriptionStatus.CANCELLED);
      subscriptionRepository.save(pendingSubscription);
      throw new PaymentInitiationException("Failed to initiate payment for subscription", e);
    }
  }
}
