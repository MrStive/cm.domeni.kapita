package com.domeni.kapita.api;

import cm.domeni.generated.domeni.kapita.api.DebtApi;
import cm.domeni.generated.domeni.kapita.dto.CreateDebtDTO;
import cm.domeni.generated.domeni.kapita.dto.CreationResponseDTO;
import cm.domeni.generated.domeni.kapita.dto.DebtDTO;
import cm.domeni.generated.domeni.kapita.dto.DebtPageDTO;
import cm.domeni.generated.domeni.kapita.dto.DebtTypeDTO;
import com.domeni.kapita.domain.user.UserId;
import com.domeni.kapita.security.jwt.CurrentUserProvider;
import com.domeni.kapita.service.DebtService;
import com.domeni.kapita.service.mapper.DebtMapper;
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
  private final DebtMapper debtMapper;

  @Override
  public ResponseEntity<DebtPageDTO> fetchDebtsByType(
      DebtTypeDTO type, Integer pageNumber, Integer pageSize) {
    return ResponseEntity.ok(
        debtMapper.map(
            debtService.getDebtsByType(
                debtMapper.map(type),
                pageNumber,
                pageSize,
                new UserId(currentUserProvider.requireCurrentUserId()))));
  }

  @Override
  public ResponseEntity<CreationResponseDTO> createDebt(CreateDebtDTO createDebtDTO) {
    UUID createdDebtId =
        debtService.createDebt(
            debtMapper.map(createDebtDTO), new UserId(currentUserProvider.requireCurrentUserId()));
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new CreationResponseDTO().newId(createdDebtId));
  }

  @Override
  public ResponseEntity<DebtDTO> markDebtAsPaid(UUID debtId) {
    return ResponseEntity.ok(
        debtMapper.map(
            debtService.markDebtAsPaid(
                debtId, new UserId(currentUserProvider.requireCurrentUserId()))));
  }
}
