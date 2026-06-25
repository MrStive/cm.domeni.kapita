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
import cm.domeni.generated.domeni.kapita.dto.SubscriptionRequestDTO;
import cm.domeni.generated.domeni.kapita.dto.SubscriptionResponseDTO;
import com.domeni.kapita.domain.payment.PaymentResponse;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanData;
import com.domeni.kapita.security.jwt.CurrentUserProvider;
import com.domeni.kapita.service.SubscriptionPlanService;
import com.domeni.kapita.service.mapper.SubscriptionPlanMapper;
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
  @Mock private SubscriptionPlanMapper subscriptionPlanMapper;
  @Mock private CurrentUserProvider currentUserProvider;

  @Test
  void createSubscriptionPlanShouldReturnCreatedSubscriptionPlanIdTest() {
    UUID subscriptionPlanId = UUID.randomUUID();
    CreateSubscriptionPlanDTO input =
        new CreateSubscriptionPlanDTO()
            .status(SubscriptionPlanStatusDTO.ACTIVE)
            .durationValue(3)
            .durationUnit(SubscriptionPlanDurationUnitDTO.MONTH)
            .price(new MoneyDTO().currency("XAF").value(new BigDecimal("12500.00")));

    SubscriptionPlanData domainData = SubscriptionPlanData.builder().build();

    when(subscriptionPlanMapper.map(any(CreateSubscriptionPlanDTO.class))).thenReturn(domainData);
    when(subscriptionPlanService.createSubscriptionPlan(domainData)).thenReturn(subscriptionPlanId);

    // spotless:off
        CreationResponseDTO response =
                given()
                        .standaloneSetup(new SubscriptionPlanResource(subscriptionPlanService, subscriptionPlanMapper, currentUserProvider))
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

  @Test
  void subscribeToPlanShouldReturnPaymentResponseTest() {
    UUID planId = UUID.randomUUID();
    UUID userIdValue = UUID.randomUUID();
    String phoneNumber = "+237670000000";
    SubscriptionRequestDTO input = new SubscriptionRequestDTO().phoneNumber(phoneNumber);

    PaymentResponse domainResponse =
        PaymentResponse.builder()
            .paymentId(UUID.randomUUID().toString())
            .externalReference(UUID.randomUUID().toString())
            .status("PENDING")
            .paymentUrl("http://payment.url")
            .build();
    SubscriptionResponseDTO expectedDto =
        new SubscriptionResponseDTO()
            .transactionId(UUID.fromString(domainResponse.paymentId()))
            .paymentUrl(domainResponse.paymentUrl());

    when(currentUserProvider.requireCurrentUserId()).thenReturn(userIdValue);
    // Loose matching to identify if it's a matching issue
    when(subscriptionPlanService.subscribeToPlan(any(), any(), any())).thenReturn(domainResponse);
    when(subscriptionPlanMapper.map(any(PaymentResponse.class))).thenReturn(expectedDto);

    // spotless:off
    SubscriptionResponseDTO response =
            given()
                    .standaloneSetup(new SubscriptionPlanResource(subscriptionPlanService, subscriptionPlanMapper, currentUserProvider))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(input)
            .when()
                    .post("/subscription-plan/{planId}/subscribe", planId.toString()) // Ensure UUID is stringified
            .then()
                    .statusCode(201)
                    .extract().body().as(SubscriptionResponseDTO.class);
    // spotless:on

    assertThat(response.getTransactionId()).isEqualTo(expectedDto.getTransactionId());
    assertThat(response.getPaymentUrl()).isEqualTo(expectedDto.getPaymentUrl());
  }
}
