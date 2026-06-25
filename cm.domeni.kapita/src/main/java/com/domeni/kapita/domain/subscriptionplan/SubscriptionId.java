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
public class SubscriptionId implements Serializable {
  private String value;

  public SubscriptionId(@NonNull UUID value) {
    this.value = value.toString();
  }

  public SubscriptionId() {
    this.value = UUID.randomUUID().toString();
  }

  public UUID toUUID() {
    return UUID.fromString(value);
  }

  public static SubscriptionId generate() {
    return new SubscriptionId(UUID.randomUUID());
  }
}
