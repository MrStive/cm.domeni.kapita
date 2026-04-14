package com.domeni.kapita.domain.transaction;

import java.util.List;

public record TransactionPage(
    List<Transaction> items, int pageNumber, int pageSize, long totalElements, int totalPages) {}
