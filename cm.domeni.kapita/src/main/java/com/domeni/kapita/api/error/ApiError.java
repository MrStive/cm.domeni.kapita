package com.domeni.kapita.api.error;

import java.time.Instant;

public record ApiError(
    String code, String message, Instant timestamp, String path, String traceId) {}
