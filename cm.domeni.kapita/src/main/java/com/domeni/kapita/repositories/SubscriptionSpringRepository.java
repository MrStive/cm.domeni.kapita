package com.domeni.kapita.repositories;

import com.domeni.kapita.domain.subscriptionplan.Subscription;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionId;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanId;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionStatus;
import com.domeni.kapita.domain.user.UserId;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubscriptionSpringRepository extends JpaRepository<Subscription, SubscriptionId> {
  Optional<Subscription> findByPaymentTransactionId(UUID paymentTransactionId);

  Optional<Subscription> findByUserIdAndPlanIdAndStatus(
      UserId userId, SubscriptionPlanId planId, SubscriptionStatus status);

  @Query(
      "SELECT s FROM Subscription s WHERE s.userId = :userId ORDER BY "
          + "CASE s.status "
          + "WHEN 'ACTIVE' THEN 0 "
          + "WHEN 'TRIAL' THEN 1 "
          + "WHEN 'PENDING' THEN 2 "
          + "WHEN 'CANCELLED' THEN 3 "
          + "WHEN 'EXPIRED' THEN 4 "
          + "END, s.createdAt DESC")
  List<Subscription> findByUserIdOrdered(@Param("userId") UserId userId);

  List<Subscription> findByStatusIn(List<SubscriptionStatus> statuses);

  @Query(
      "SELECT s FROM Subscription s "
          + "WHERE (s.status = com.domeni.kapita.domain.subscriptionplan.SubscriptionStatus.ACTIVE "
          + "AND s.endDate IS NOT NULL AND s.endDate < :now) "
          + "OR (s.status = com.domeni.kapita.domain.subscriptionplan.SubscriptionStatus.TRIAL "
          + "AND s.trialEndDate IS NOT NULL AND s.trialEndDate < :now)")
  List<Subscription> findExpiredSubscriptions(@Param("now") LocalDateTime now);

  boolean existsByUserIdAndTrialEndDateIsNotNull(UserId userId);
}
