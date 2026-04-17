package com.domeni.kapita.api;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import cm.domeni.generated.domeni.kapita.dto.CreateSubscriptionPlanDTO;
import cm.domeni.generated.domeni.kapita.dto.CreationResponseDTO;
import cm.domeni.generated.domeni.kapita.dto.MoneyDTO;
import cm.domeni.generated.domeni.kapita.dto.SubscriptionPlanDurationUnitDTO;
import cm.domeni.generated.domeni.kapita.dto.SubscriptionPlanStatusDTO;
import com.domeni.kapita.service.SubscriptionPlanService;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

@ExtendWith(MockitoExtension.class)
class SubscriptionPlanResourceTest {
  @Mock private SubscriptionPlanService subscriptionPlanService;

  @Test
  void createSubscriptionPlanShouldReturnCreatedSubscriptionPlanIdTest() {
    UUID subscriptionPlanId = UUID.randomUUID();
    CreateSubscriptionPlanDTO input =
        new CreateSubscriptionPlanDTO()
            .status(SubscriptionPlanStatusDTO.ACTIVE)
            .durationValue(3)
            .durationUnit(SubscriptionPlanDurationUnitDTO.MONTH)
            .price(new MoneyDTO().currency("XAF").value(new BigDecimal("12500.00")));

    when(subscriptionPlanService.createSubscriptionPlan(any(CreateSubscriptionPlanDTO.class)))
        .thenReturn(subscriptionPlanId);

    // spotless:off
        CreationResponseDTO response =
                given()
                        .standaloneSetup(new SubscriptionPlanResource(subscriptionPlanService))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .body(input)
                .when()
                        .post("/subscription-plan")
                .then()
                        .statusCode(201)
                        .extract().body().as(CreationResponseDTO.class);
        // spotless:on
    assertThat(response.getNewId()).isEqualTo(subscriptionPlanId);
  }
}
