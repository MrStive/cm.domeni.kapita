package com.domeni.kapita.domain.debt;

import java.time.LocalDate;
import javax.money.MonetaryAmount;
import lombok.Builder;

@Builder
public record DebtData(
    DebtType type, String counterpartyName, MonetaryAmount amount, LocalDate dueDate) {}
