package com.domeni.kapita.api;

import cm.domeni.generated.domeni.kapita.api.SubscriptionPlanApi;
import cm.domeni.generated.domeni.kapita.dto.CreateSubscriptionPlanDTO;
import cm.domeni.generated.domeni.kapita.dto.CreationResponseDTO;
import com.domeni.kapita.service.SubscriptionPlanService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SubscriptionPlanResource implements SubscriptionPlanApi {
  private final SubscriptionPlanService subscriptionPlanService;

  @Override
  public ResponseEntity<CreationResponseDTO> createSubscriptionPlan(
      CreateSubscriptionPlanDTO createSubscriptionPlanDTO) {
    UUID createdSubscriptionPlanId =
        subscriptionPlanService.createSubscriptionPlan(createSubscriptionPlanDTO);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new CreationResponseDTO().newId(createdSubscriptionPlanId));
  }
}
