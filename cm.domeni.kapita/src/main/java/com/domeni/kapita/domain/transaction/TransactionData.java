package com.domeni.kapita.domain.transaction;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record TransactionData(
    TransactionType type,
    TransactionCategory category,
    String otherCategoryDetail,
    BigDecimal amount,
    String description) {}
