package com.domeni.kapita.domain.transaction;

import java.time.LocalDate;
import javax.money.MonetaryAmount;

public record TransactionBalance(LocalDate startDate, LocalDate endDate, MonetaryAmount balance) {}
