package com.domeni.kapita.api;

import cm.domeni.generated.domeni.kapita.api.SubscriptionPlanApi;
import cm.domeni.generated.domeni.kapita.dto.CreateSubscriptionPlanDTO;
import cm.domeni.generated.domeni.kapita.dto.CreationResponseDTO;
import cm.domeni.generated.domeni.kapita.dto.SubscriptionPlanDTO;
import cm.domeni.generated.domeni.kapita.dto.SubscriptionRequestDTO;
import cm.domeni.generated.domeni.kapita.dto.SubscriptionResponseDTO;
import com.domeni.kapita.domain.user.UserId;
import com.domeni.kapita.security.jwt.CurrentUserProvider;
import com.domeni.kapita.service.SubscriptionPlanService;
import com.domeni.kapita.service.mapper.SubscriptionPlanMapper;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SubscriptionPlanResource implements SubscriptionPlanApi {
  private final SubscriptionPlanService subscriptionPlanService;
  private final SubscriptionPlanMapper subscriptionPlanMapper;
  private final CurrentUserProvider currentUserProvider;

  @Override
  public ResponseEntity<List<SubscriptionPlanDTO>> fetchSubscriptionPlans() {
    return ResponseEntity.ok(subscriptionPlanService.getActivePlans());
  }

  @Override
  public ResponseEntity<CreationResponseDTO> createSubscriptionPlan(
      CreateSubscriptionPlanDTO createSubscriptionPlanDTO) {
    UUID createdSubscriptionPlanId =
        subscriptionPlanService.createSubscriptionPlan(
            subscriptionPlanMapper.map(createSubscriptionPlanDTO));
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new CreationResponseDTO().newId(createdSubscriptionPlanId));
  }

  @Override
  public ResponseEntity<SubscriptionResponseDTO> subscribeToPlan(
      UUID planId, SubscriptionRequestDTO subscriptionRequestDTO) {
    UserId userId = new UserId(currentUserProvider.requireCurrentUserId());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            subscriptionPlanMapper.map(
                subscriptionPlanService.subscribeToPlan(
                    planId, userId, subscriptionRequestDTO.getPhoneNumber())));
  }
}
