package com.domeni.kapita.domain.exception;

public class InvalidDemoPayloadException extends DomainException {
  public static final String CODE = "KAPITA-400-002";

  public InvalidDemoPayloadException(String message) {
    super(CODE, message);
  }

  public InvalidDemoPayloadException() {
    this("invalid demo payload");
  }
}
