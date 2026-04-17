package com.domeni.kapita.domain.subscriptionplan;

import com.domeni.kapita.domain.common.MonetaryAmountConverter;
import com.domeni.kapita.domain.core.SoftDeleteJpaEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.money.MonetaryAmount;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@FieldNameConstants
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "t_subscription_plan")
public class SubscriptionPlan extends SoftDeleteJpaEntity<SubscriptionPlanId> {

  @EmbeddedId
  @AttributeOverride(name = "value", column = @Column(name = "c_id"))
  private SubscriptionPlanId id = new SubscriptionPlanId();

  @Enumerated(EnumType.STRING)
  @Column(name = "c_status", nullable = false)
  private SubscriptionPlanStatus status;

  @Column(name = "c_duration_value", nullable = false)
  private Integer durationValue;

  @Enumerated(EnumType.STRING)
  @Column(name = "c_duration_unit", nullable = false)
  private SubscriptionPlanDurationUnit durationUnit;

  @Convert(converter = MonetaryAmountConverter.class)
  @Column(name = "c_price", nullable = false, length = 64)
  private MonetaryAmount price;

  @Column(name = "c_created_at", nullable = false)
  private LocalDateTime createdAt;

  @Builder
  public SubscriptionPlan(
      SubscriptionPlanId id,
      SubscriptionPlanStatus status,
      Integer durationValue,
      SubscriptionPlanDurationUnit durationUnit,
      MonetaryAmount price,
      LocalDateTime createdAt) {
    this.id = id != null ? id : new SubscriptionPlanId();
    this.status = status;
    this.durationValue = durationValue;
    this.durationUnit = durationUnit;
    this.price = price;
    this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (!(object instanceof SubscriptionPlan subscriptionPlan)) {
      return false;
    }
    return Objects.equals(id, subscriptionPlan.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
