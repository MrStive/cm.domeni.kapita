package com.domeni.kapita.api;

import cm.domeni.generated.domeni.kapita.api.DebtApi;
import cm.domeni.generated.domeni.kapita.dto.CreateDebtDTO;
import cm.domeni.generated.domeni.kapita.dto.CreationResponseDTO;
import com.domeni.kapita.domain.user.UserId;
import com.domeni.kapita.security.jwt.CurrentUserProvider;
import com.domeni.kapita.service.DebtService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DebtResource implements DebtApi {
  private final CurrentUserProvider currentUserProvider;
  private final DebtService debtService;

  @Override
  public ResponseEntity<CreationResponseDTO> createDebt(CreateDebtDTO createDebtDTO) {
    UUID createdDebtId =
        debtService.createDebt(
            createDebtDTO, new UserId(currentUserProvider.requireCurrentUserId()));
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new CreationResponseDTO().newId(createdDebtId));
  }
}
