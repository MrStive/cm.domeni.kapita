package com.domeni.kapita.domain.transaction;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Embeddable
@Getter
@EqualsAndHashCode
public class TransactionId implements Serializable {
  private String value;

  public TransactionId(@NonNull UUID value) {
    this.value = value.toString();
  }

  public TransactionId() {
    this.value = UUID.randomUUID().toString();
  }

  public UUID toUUID() {
    return UUID.fromString(value);
  }
}
