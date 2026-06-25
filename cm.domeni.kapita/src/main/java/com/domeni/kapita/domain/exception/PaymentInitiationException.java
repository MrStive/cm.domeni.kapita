package com.domeni.kapita.domain.exception;

public class PaymentInitiationException extends RuntimeException {
  public PaymentInitiationException(String message, Throwable cause) {
    super(message, cause);
  }

  public PaymentInitiationException(String message) {
    super(message);
  }
}
