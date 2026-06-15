package com.domeni.kapita.domain.payment;

import lombok.Builder;
import org.jspecify.annotations.Nullable;

@Builder
public record PaymentResponse(
    String paymentId, String externalReference, String status, @Nullable String paymentUrl) {}
