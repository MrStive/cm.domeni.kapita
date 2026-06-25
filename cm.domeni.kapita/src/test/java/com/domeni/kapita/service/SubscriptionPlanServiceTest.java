package com.domeni.kapita.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import com.domeni.kapita.domain.exception.PaymentInitiationException;
import com.domeni.kapita.domain.exception.SubscriptionPlanNotFoundException;
import com.domeni.kapita.domain.payment.PaymentPort;
import com.domeni.kapita.domain.payment.PaymentRequest;
import com.domeni.kapita.domain.payment.PaymentResponse;
import com.domeni.kapita.domain.subscriptionplan.Subscription;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionData;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionFactory;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionId;
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
import javax.money.MonetaryAmount;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SubscriptionPlanServiceTest {

  @Mock private SubscriptionPlanFactory subscriptionPlanFactory;
  @Mock private SubscriptionPlanFetcher subscriptionPlanFetcher;
  @Mock private SubscriptionFactory subscriptionFactory;
  @Mock private SubscriptionRepository subscriptionRepository;
  @Mock private PaymentPort paymentPort;

  @InjectMocks private SubscriptionPlanService subscriptionPlanService;

  @Test
  void createSubscriptionPlanShouldDelegateDataToFactoryAndReturnCreatedIdTest() {
    SubscriptionPlanData input = SubscriptionPlanData.builder().build();
    UUID expectedId = UUID.randomUUID();
    SubscriptionPlan createdSubscriptionPlan = new SubscriptionPlan();
    createdSubscriptionPlan.setId(new SubscriptionPlanId(expectedId));

    given(subscriptionPlanFactory.create(input)).willReturn(createdSubscriptionPlan);

    UUID result = subscriptionPlanService.createSubscriptionPlan(input);

    assertThat(result).isEqualTo(expectedId);
    then(subscriptionPlanFactory).should().create(input);
  }

  @Test
  void createSubscriptionPlanWhenFactoryReturnsPlanWithoutIdShouldThrowIllegalStateExceptionTest() {
    SubscriptionPlanData input = SubscriptionPlanData.builder().build();
    SubscriptionPlan createdSubscriptionPlan = new SubscriptionPlan();
    createdSubscriptionPlan.setId(null);
    given(subscriptionPlanFactory.create(input)).willReturn(createdSubscriptionPlan);

    assertThatThrownBy(() -> subscriptionPlanService.createSubscriptionPlan(input))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("created subscription plan has no identifier");

    then(subscriptionPlanFactory).should().create(input);
  }

  @Test
  void subscribeToPlanShouldInitiatePaymentWhenNoPendingSubscriptionExistsTest() {
    UUID planId = UUID.randomUUID();
    UserId userId = new UserId(UUID.randomUUID());
    String phoneNumber = "+237670000000";
    SubscriptionPlanId subscriptionPlanId = new SubscriptionPlanId(planId);
    MonetaryAmount price = Money.of(1000, "XAF");

    SubscriptionPlan plan = mock(SubscriptionPlan.class);
    given(plan.getId()).willReturn(subscriptionPlanId);
    given(plan.getPrice()).willReturn(price);

    Subscription sub = mock(Subscription.class);
    given(sub.getId()).willReturn(new SubscriptionId(UUID.randomUUID()));

    given(
            subscriptionRepository.findByUserIdAndPlanIdAndStatus(
                userId, subscriptionPlanId, SubscriptionStatus.PENDING))
        .willReturn(Optional.empty());
    given(subscriptionPlanFetcher.getById(subscriptionPlanId)).willReturn(Optional.of(plan));
    given(subscriptionFactory.create(any(SubscriptionData.class))).willReturn(sub);

    PaymentResponse paymentResponse =
        PaymentResponse.builder()
            .paymentId(UUID.randomUUID().toString())
            .externalReference(UUID.randomUUID().toString())
            .status("PENDING")
            .paymentUrl("http://payment.url")
            .build();
    given(paymentPort.initiatePayment(any(PaymentRequest.class))).willReturn(paymentResponse);

    PaymentResponse result = subscriptionPlanService.subscribeToPlan(planId, userId, phoneNumber);

    assertThat(result).isEqualTo(paymentResponse);
    then(sub).should().setPaymentTransactionId(UUID.fromString(paymentResponse.paymentId()));
    then(subscriptionRepository).should().save(sub);
  }

  @Test
  void subscribeToPlanShouldReturnExistingTransactionWhenPendingSubscriptionExistsTest() {
    UUID planId = UUID.randomUUID();
    UserId userId = new UserId(UUID.randomUUID());
    SubscriptionPlanId subscriptionPlanId = new SubscriptionPlanId(planId);
    UUID transactionId = UUID.randomUUID();

    Subscription existingSub = mock(Subscription.class);
    given(existingSub.getId()).willReturn(new SubscriptionId(UUID.randomUUID()));
    given(existingSub.getPaymentTransactionId()).willReturn(transactionId);

    given(
            subscriptionRepository.findByUserIdAndPlanIdAndStatus(
                userId, subscriptionPlanId, SubscriptionStatus.PENDING))
        .willReturn(Optional.of(existingSub));

    PaymentResponse result = subscriptionPlanService.subscribeToPlan(planId, userId, "phone");

    assertThat(result.paymentId()).isEqualTo(transactionId.toString());
    then(paymentPort).shouldHaveNoInteractions();
    then(subscriptionPlanFetcher).shouldHaveNoInteractions();
  }

  @Test
  void subscribeToPlanWhenPlanNotFoundShouldThrowExceptionTest() {
    UUID planId = UUID.randomUUID();
    UserId userId = new UserId(UUID.randomUUID());
    SubscriptionPlanId subscriptionPlanId = new SubscriptionPlanId(planId);

    given(
            subscriptionRepository.findByUserIdAndPlanIdAndStatus(
                userId, subscriptionPlanId, SubscriptionStatus.PENDING))
        .willReturn(Optional.empty());
    given(subscriptionPlanFetcher.getById(subscriptionPlanId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> subscriptionPlanService.subscribeToPlan(planId, userId, "phone"))
        .isInstanceOf(SubscriptionPlanNotFoundException.class)
        .hasMessageContaining(planId.toString());
  }

  @Test
  void subscribeToPlanWhenPaymentFailsShouldMarkAsCancelledAndThrowExceptionTest() {
    UUID planId = UUID.randomUUID();
    UserId userId = new UserId(UUID.randomUUID());
    SubscriptionPlanId subscriptionPlanId = new SubscriptionPlanId(planId);

    SubscriptionPlan plan = mock(SubscriptionPlan.class);
    given(plan.getId()).willReturn(subscriptionPlanId);
    given(plan.getPrice()).willReturn(Money.of(1000, "XAF"));

    Subscription sub = mock(Subscription.class);
    given(sub.getId()).willReturn(new SubscriptionId(UUID.randomUUID()));

    given(
            subscriptionRepository.findByUserIdAndPlanIdAndStatus(
                userId, subscriptionPlanId, SubscriptionStatus.PENDING))
        .willReturn(Optional.empty());
    given(subscriptionPlanFetcher.getById(subscriptionPlanId)).willReturn(Optional.of(plan));
    given(subscriptionFactory.create(any(SubscriptionData.class))).willReturn(sub);
    given(paymentPort.initiatePayment(any(PaymentRequest.class)))
        .willThrow(new RuntimeException("API Error"));

    assertThatThrownBy(
            () -> subscriptionPlanService.subscribeToPlan(planId, userId, "+237670000000"))
        .isInstanceOf(PaymentInitiationException.class);

    then(sub).should().setStatus(SubscriptionStatus.CANCELLED);
    then(subscriptionRepository).should().save(sub);
  }
}
