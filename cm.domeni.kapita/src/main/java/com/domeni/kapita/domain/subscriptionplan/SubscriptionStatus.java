package com.domeni.kapita.domain.subscriptionplan;

public enum SubscriptionStatus {
  PENDING,
  ACTIVE,
  EXPIRED,
  CANCELLED,
  TRIAL;

  public int sortOrder() {
    return switch (this) {
      case ACTIVE -> 0;
      case TRIAL -> 1;
      case PENDING -> 2;
      case CANCELLED -> 3;
      case EXPIRED -> 4;
    };
  }
}
