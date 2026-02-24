package com.domeni.kapita.api.error;

import com.domeni.kapita.domain.exception.DemoNotFoundException;
import com.domeni.kapita.domain.exception.DomainException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class ApiExceptionHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);
  private static final String VALIDATION_ERROR_CODE = "KAPITA-400-VALIDATION";
  private static final String DOMAIN_ERROR_CODE = "KAPITA-400-001";
  private static final String RESOURCE_NOT_FOUND_ERROR_CODE = "KAPITA-404-000";
  private static final String INTERNAL_ERROR_CODE = "KAPITA-500-001";

  @ExceptionHandler(DemoNotFoundException.class)
  public ResponseEntity<ApiError> handleDemoNotFound(
      DemoNotFoundException exception, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(buildError(exception.getCode(), exception.getMessage(), request));
  }

  @ExceptionHandler(DomainException.class)
  public ResponseEntity<ApiError> handleDomainException(
      DomainException exception, HttpServletRequest request) {
    String code =
        Optional.ofNullable(exception.getCode())
            .filter(value -> !value.isBlank())
            .orElse(DOMAIN_ERROR_CODE);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(buildError(code, exception.getMessage(), request));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidationException(
      MethodArgumentNotValidException exception, HttpServletRequest request) {
    String message =
        exception.getBindingResult().getFieldErrors().stream()
            .findFirst()
            .map(fieldError -> fieldError.getField() + " " + fieldError.getDefaultMessage())
            .orElse("request validation failed");
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(buildError(VALIDATION_ERROR_CODE, message, request));
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ApiError> handleNoResourceFoundException(
      NoResourceFoundException exception, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(buildError(RESOURCE_NOT_FOUND_ERROR_CODE, "resource not found", request));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleUnhandledException(
      Exception exception, HttpServletRequest request) {
    LOGGER.error("Unhandled exception for path {}", request.getRequestURI(), exception);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(buildError(INTERNAL_ERROR_CODE, "internal server error", request));
  }

  private ApiError buildError(String code, String message, HttpServletRequest request) {
    return new ApiError(
        code, message, Instant.now(), request.getRequestURI(), resolveTraceId(request));
  }

  private String resolveTraceId(HttpServletRequest request) {
    return Optional.ofNullable(MDC.get("traceId"))
        .filter(value -> !value.isBlank())
        .or(
            () ->
                Optional.ofNullable(request.getHeader("X-Request-Id"))
                    .filter(value -> !value.isBlank()))
        .or(() -> Optional.ofNullable(request.getRequestId()).filter(value -> !value.isBlank()))
        .orElse(UUID.randomUUID().toString());
  }
}
