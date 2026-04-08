package com.domeni.kapita.domain.user;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Embeddable
@Getter
@EqualsAndHashCode
public class UserId implements Serializable {
  private String value;

  public UserId(@NonNull UUID value) {
    this.value = value.toString();
  }

  public UserId() {
    this.value = UUID.randomUUID().toString();
  }

  public UUID toUUID() {
    return UUID.fromString(value);
  }
}
