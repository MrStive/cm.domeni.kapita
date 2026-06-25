package com.domeni.kapita.domain.subscriptionplan;

import com.domeni.kapita.domain.core.SoftDeleteJpaEntity;
import com.domeni.kapita.domain.user.UserId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.lang.Nullable;

@FieldNameConstants
@Getter
@Setter
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

  @Builder
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
