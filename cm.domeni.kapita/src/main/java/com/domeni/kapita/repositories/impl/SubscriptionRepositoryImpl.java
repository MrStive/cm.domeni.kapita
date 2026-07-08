package com.domeni.kapita.repositories.impl;

import com.domeni.kapita.domain.subscriptionplan.Subscription;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanId;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionRepository;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionStatus;
import com.domeni.kapita.domain.user.UserId;
import com.domeni.kapita.repositories.SubscriptionSpringRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SubscriptionRepositoryImpl implements SubscriptionRepository {

  private final SubscriptionSpringRepository subscriptionSpringRepository;

  @Override
  public Subscription save(Subscription subscription) {
    return subscriptionSpringRepository.save(subscription);
  }

  @Override
  public Optional<Subscription> findByPaymentTransactionId(UUID paymentTransactionId) {
    return subscriptionSpringRepository.findByPaymentTransactionId(paymentTransactionId);
  }

  @Override
  public Optional<Subscription> findByUserIdAndPlanIdAndStatus(
      UserId userId, SubscriptionPlanId planId, SubscriptionStatus status) {
    return subscriptionSpringRepository.findByUserIdAndPlanIdAndStatus(userId, planId, status);
  }

  @Override
  public List<Subscription> findByUserId(UserId userId) {
    return subscriptionSpringRepository.findByUserId(userId);
  }

  @Override
  public boolean hasUserEverHadTrial(UserId userId) {
    return subscriptionSpringRepository.existsByUserIdAndTrialEndDateIsNotNull(userId);
  }

  @Override
  public List<Subscription> findExpiredSubscriptions(LocalDateTime now) {
    return subscriptionSpringRepository.findExpiredSubscriptions(now);
  }
}
