package com.domeni.kapita.domain.debt;

import java.util.List;

public record DebtPage(
    List<Debt> items, int pageNumber, int pageSize, long totalElements, int totalPages) {}
