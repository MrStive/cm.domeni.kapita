package com.domeni.kapita.domain.exception;

public class InvalidSubscriptionPlanPayloadException extends DomainException {
  public static final String CODE = "KAPITA-400-006";

  public InvalidSubscriptionPlanPayloadException(String message) {
    super(CODE, message);
  }

  public InvalidSubscriptionPlanPayloadException() {
    this("invalid subscription plan payload");
  }
}
