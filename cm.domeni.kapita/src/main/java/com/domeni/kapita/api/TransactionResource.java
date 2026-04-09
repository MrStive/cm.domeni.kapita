package com.domeni.kapita.api;

import cm.domeni.generated.domeni.kapita.api.TransactionApi;
import cm.domeni.generated.domeni.kapita.dto.CreateTransactionDTO;
import cm.domeni.generated.domeni.kapita.dto.CreationResponseDTO;
import com.domeni.kapita.domain.user.UserId;
import com.domeni.kapita.security.jwt.CurrentUserProvider;
import com.domeni.kapita.service.TransactionService;
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

  @Override
  public ResponseEntity<CreationResponseDTO> createTransaction(
      CreateTransactionDTO createTransactionDTO) {
    UUID createdTransactionId =
        transactionService.createTransaction(
            createTransactionDTO, new UserId(currentUserProvider.requireCurrentUserId()));
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new CreationResponseDTO().newId(createdTransactionId));
  }
}
