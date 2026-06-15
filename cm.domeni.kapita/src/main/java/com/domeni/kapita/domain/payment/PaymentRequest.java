package com.domeni.kapita.domain.payment;

import java.math.BigDecimal;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@Builder
public record PaymentRequest(
    BigDecimal amount,
    String currency,
    String description,
    String phoneNumber,
    PaymentProvider provider,
    String externalReference,
    String purpose,
    String idempotencyKey,
    @Nullable String returnUrl) {}
