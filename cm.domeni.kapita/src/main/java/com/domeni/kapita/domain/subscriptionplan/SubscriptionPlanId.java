package com.domeni.kapita.domain.subscriptionplan;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Embeddable
@Getter
@EqualsAndHashCode
public class SubscriptionPlanId implements Serializable {
  private String value;

  public SubscriptionPlanId(@NonNull UUID value) {
    this.value = value.toString();
  }

  public SubscriptionPlanId() {
    this.value = UUID.randomUUID().toString();
  }

  public UUID toUUID() {
    return UUID.fromString(value);
  }
}
