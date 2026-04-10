package com.domeni.kapita.domain.exception;

public class InvalidDebtPayloadException extends DomainException {
  public static final String CODE = "KAPITA-400-005";

  public InvalidDebtPayloadException(String message) {
    super(CODE, message);
  }

  public InvalidDebtPayloadException() {
    this("invalid debt payload");
  }
}
