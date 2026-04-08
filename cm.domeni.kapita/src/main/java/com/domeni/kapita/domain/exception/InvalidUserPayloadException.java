package com.domeni.kapita.domain.exception;

public class InvalidUserPayloadException extends DomainException {
  public static final String CODE = "KAPITA-400-003";

  public InvalidUserPayloadException(String message) {
    super(CODE, message);
  }

  public InvalidUserPayloadException() {
    this("invalid user payload");
  }
}
