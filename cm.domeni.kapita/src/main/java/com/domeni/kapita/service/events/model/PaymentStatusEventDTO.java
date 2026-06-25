package com.domeni.kapita.service.events.model;

import com.domeni.kapita.domain.payment.PaymentStatus;
import java.math.BigDecimal;
import java.time.Instant;
import org.jspecify.annotations.Nullable;

public record PaymentStatusEventDTO(
    String paymentId,
    String externalReference,
    String purpose,
    String userId,
    PaymentStatus status,
    BigDecimal amount,
    String currency,
    String provider,
    String providerAttemptId,
    @Nullable String providerReference,
    @Nullable String failureReason,
    Instant occurredAt) {}
