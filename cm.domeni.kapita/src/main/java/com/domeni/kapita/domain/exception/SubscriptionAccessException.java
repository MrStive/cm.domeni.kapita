package com.domeni.kapita.domain.exception;

public class SubscriptionAccessException extends DomainException {

  private static final String CODE = "KAPITA-403-SUBSCRIPTION-NO-ACTIVE";

  public SubscriptionAccessException(String message) {
    super(CODE, message);
  }

  public SubscriptionAccessException(String message, Throwable cause) {
    super(CODE, message, cause);
  }
}
