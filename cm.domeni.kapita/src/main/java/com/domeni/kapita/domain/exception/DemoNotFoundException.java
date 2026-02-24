package com.domeni.kapita.domain.exception;

public class DemoNotFoundException extends DomainException {
  public static final String CODE = "KAPITA-404-001";

  public DemoNotFoundException(String message) {
    super(CODE, message);
  }

  public DemoNotFoundException() {
    this("demo not found");
  }
}
