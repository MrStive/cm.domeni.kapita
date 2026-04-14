package com.domeni.kapita.domain.exception;

public class DebtNotFoundException extends DomainException {
  public static final String CODE = "KAPITA-404-002";

  public DebtNotFoundException(String message) {
    super(CODE, message);
  }

  public DebtNotFoundException() {
    this("debt not found");
  }
}
