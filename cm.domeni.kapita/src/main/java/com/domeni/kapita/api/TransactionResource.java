package com.domeni.kapita.api;

import cm.domeni.generated.domeni.kapita.api.TransactionApi;
import cm.domeni.generated.domeni.kapita.dto.CreateTransactionDTO;
import cm.domeni.generated.domeni.kapita.dto.CreationResponseDTO;
import cm.domeni.generated.domeni.kapita.dto.MoneyDTO;
import cm.domeni.generated.domeni.kapita.dto.TransactionAmountGroupedDTO;
import cm.domeni.generated.domeni.kapita.dto.TransactionPageDTO;
import cm.domeni.generated.domeni.kapita.dto.TransactionTypeDTO;
import com.domeni.kapita.domain.exception.InvalidTransactionPayloadException;
import com.domeni.kapita.domain.user.UserId;
import com.domeni.kapita.security.jwt.CurrentUserProvider;
import com.domeni.kapita.service.TransactionService;
import com.domeni.kapita.service.mapper.TransactionMapper;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TransactionResource implements TransactionApi {
  private final CurrentUserProvider currentUserProvider;
  private final TransactionService transactionService;
  private final TransactionMapper transactionMapper;

  @Override
  public ResponseEntity<CreationResponseDTO> createTransaction(
      CreateTransactionDTO createTransactionDTO) {
    UUID createdTransactionId =
        transactionService.createTransaction(
            transactionMapper.map(createTransactionDTO),
            new UserId(currentUserProvider.requireCurrentUserId()));
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new CreationResponseDTO().newId(createdTransactionId));
  }

  @Override
  public ResponseEntity<TransactionPageDTO> fetchTransactions(
      TransactionTypeDTO type, Integer pageNumber, Integer pageSize) {
    return ResponseEntity.ok(
        transactionMapper.map(
            transactionService.getTransactions(
                transactionMapper.map(type),
                pageNumber,
                pageSize,
                new UserId(currentUserProvider.requireCurrentUserId()))));
  }

  @Override
  public ResponseEntity<MoneyDTO> fetchTransactionBalance(LocalDate startDate, LocalDate endDate) {
    return ResponseEntity.ok(
        transactionMapper.map(
            transactionService.getBalance(
                startDate, endDate, new UserId(currentUserProvider.requireCurrentUserId()))));
  }

  @Override
  public ResponseEntity<TransactionAmountGroupedDTO> fetchTransactionAmountByType(
      LocalDate startDate, LocalDate endDate, TransactionTypeDTO type) {
    if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
      throw new InvalidTransactionPayloadException("transaction period is invalid");
    }
    UserId currentUserId = new UserId(currentUserProvider.requireCurrentUserId());
    if (type == null) {
      return ResponseEntity.ok(
          transactionMapper.map(
              transactionService.getAmountsGroupedByType(startDate, endDate, currentUserId)));
    }
    return ResponseEntity.ok(
        transactionMapper.map(
            transactionService.getAmountByType(
                startDate, endDate, transactionMapper.map(type), currentUserId),
            transactionMapper.map(type)));
  }
}
