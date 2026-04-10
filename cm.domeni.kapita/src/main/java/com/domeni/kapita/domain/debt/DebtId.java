package com.domeni.kapita.domain.debt;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Embeddable
@Getter
@EqualsAndHashCode
public class DebtId implements Serializable {
  private String value;

  public DebtId(@NonNull UUID value) {
    this.value = value.toString();
  }

  public DebtId() {
    this.value = UUID.randomUUID().toString();
  }

  public UUID toUUID() {
    return UUID.fromString(value);
  }
}
