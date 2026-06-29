package com.domeni.kapita.domain.subscriptionplan;

import com.domeni.kapita.domain.core.SoftDeleteJpaEntity;
import com.domeni.kapita.domain.exception.SubscriptionAccessException;
import com.domeni.kapita.domain.user.UserId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.lang.Nullable;

@FieldNameConstants
@Getter
@NoArgsConstructor
@Entity
@Table(name = "t_subscription")
public class Subscription extends SoftDeleteJpaEntity<SubscriptionId> {

  @EmbeddedId
  @AttributeOverride(name = "value", column = @Column(name = "c_id"))
  private SubscriptionId id = new SubscriptionId();

  @Embedded
  @AttributeOverride(name = "value", column = @Column(name = "c_user_id", nullable = false))
  private UserId userId;

  @Embedded
  @AttributeOverride(name = "value", column = @Column(name = "c_plan_id"))
  @Nullable
  private SubscriptionPlanId planId;

  @Enumerated(EnumType.STRING)
  @Column(name = "c_status", nullable = false)
  private SubscriptionStatus status;

  @Column(name = "c_payment_transaction_id")
  private UUID paymentTransactionId;

  @Column(name = "c_start_date")
  private LocalDateTime startDate;

  @Column(name = "c_end_date")
  private LocalDateTime endDate;

  @Column(name = "c_trial_end_date")
  @Nullable
  private LocalDateTime trialEndDate;

  @Column(name = "c_created_at", nullable = false)
  private LocalDateTime createdAt;

  public Subscription(
      SubscriptionId id,
      UserId userId,
      @Nullable SubscriptionPlanId planId,
      SubscriptionStatus status,
      UUID paymentTransactionId,
      LocalDateTime startDate,
      LocalDateTime endDate,
      @Nullable LocalDateTime trialEndDate,
      LocalDateTime createdAt) {
    this.id = id != null ? id : new SubscriptionId();
    this.userId = userId;
    this.planId = planId;
    this.status = status;
    this.paymentTransactionId = paymentTransactionId;
    this.startDate = startDate;
    this.endDate = endDate;
    this.trialEndDate = trialEndDate;
    this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
  }

  public static void ensureWriteAccess(
      SubscriptionStatus status,
      @Nullable LocalDateTime trialEndDate,
      @Nullable LocalDateTime endDate,
      LocalDateTime now) {
    switch (status) {
      case TRIAL -> {
        if (trialEndDate != null && now.isAfter(trialEndDate)) {
          throw new SubscriptionAccessException("Votre période d'essai a expiré.");
        }
        throw new SubscriptionAccessException("Accès en lecture seule pendant la période d'essai.");
      }
      case ACTIVE -> {
        if (endDate != null && now.isAfter(endDate)) {
          throw new SubscriptionAccessException("Votre abonnement a expiré.");
        }
      }
      case EXPIRED, CANCELLED ->
          throw new SubscriptionAccessException("Votre abonnement n'est plus actif.");
      case PENDING ->
          throw new SubscriptionAccessException("Votre abonnement est en attente de validation.");
    }
  }

  public void ensureWriteAccess(LocalDateTime now) {
    ensureWriteAccess(this.status, this.trialEndDate, this.endDate, now);
  }

  public boolean isExpired(LocalDateTime now) {
    return switch (status) {
      case ACTIVE -> endDate != null && now.isAfter(endDate);
      case TRIAL -> trialEndDate != null && now.isAfter(trialEndDate);
      default -> false;
    };
  }

  public boolean expire(Clock clock) {
    if (status == SubscriptionStatus.EXPIRED) {
      return false;
    }
    if (!isExpired(LocalDateTime.now(clock))) {
      throw new SubscriptionAccessException(
          "L'abonnement " + id.toUUID() + " n'est pas encore expiré.");
    }
    this.status = SubscriptionStatus.EXPIRED;
    return true;
  }

  public void activate(LocalDateTime now, LocalDateTime endDate) {
    if (status != SubscriptionStatus.PENDING && status != SubscriptionStatus.TRIAL) {
      throw new SubscriptionAccessException(
          "Impossible d'activer un abonnement avec le statut : " + status);
    }
    this.status = SubscriptionStatus.ACTIVE;
    this.startDate = now;
    this.endDate = endDate;
  }

  public void cancel() {
    if (status == SubscriptionStatus.CANCELLED || status == SubscriptionStatus.EXPIRED) {
      return;
    }
    this.status = SubscriptionStatus.CANCELLED;
  }

  public void assignPaymentTransaction(UUID transactionId) {
    this.paymentTransactionId = transactionId;
  }

  @Override
  public void setId(SubscriptionId id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Subscription that)) {
      return false;
    }
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
