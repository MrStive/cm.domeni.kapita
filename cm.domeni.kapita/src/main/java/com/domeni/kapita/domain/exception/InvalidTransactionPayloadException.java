package com.domeni.kapita.domain.exception;

public class InvalidTransactionPayloadException extends DomainException {
  public static final String CODE = "KAPITA-400-004";

  public InvalidTransactionPayloadException(String message) {
    super(CODE, message);
  }

  public InvalidTransactionPayloadException() {
    this("invalid transaction payload");
  }
}
