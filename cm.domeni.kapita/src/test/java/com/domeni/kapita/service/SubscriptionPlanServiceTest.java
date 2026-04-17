package com.domeni.kapita.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import cm.domeni.generated.domeni.kapita.dto.CreateSubscriptionPlanDTO;
import com.domeni.kapita.domain.exception.InvalidSubscriptionPlanPayloadException;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlan;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanData;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanFactory;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanId;
import com.domeni.kapita.service.mapper.SubscriptionPlanMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SubscriptionPlanServiceTest {

  @Mock private SubscriptionPlanFactory subscriptionPlanFactory;
  @Mock private SubscriptionPlanMapper subscriptionPlanMapper;

  @InjectMocks private SubscriptionPlanService subscriptionPlanService;

  @Test
  void createSubscriptionPlanShouldDelegateMappedDataToFactoryAndReturnCreatedIdTest() {
    CreateSubscriptionPlanDTO input = new CreateSubscriptionPlanDTO();
    SubscriptionPlanData mappedData = SubscriptionPlanData.builder().build();
    UUID expectedId = UUID.randomUUID();
    SubscriptionPlan createdSubscriptionPlan = new SubscriptionPlan();
    createdSubscriptionPlan.setId(new SubscriptionPlanId(expectedId));

    given(subscriptionPlanMapper.map(input)).willReturn(mappedData);
    given(subscriptionPlanFactory.create(mappedData)).willReturn(createdSubscriptionPlan);

    UUID result = subscriptionPlanService.createSubscriptionPlan(input);

    assertThat(result).isEqualTo(expectedId);
    then(subscriptionPlanMapper).should().map(input);
    then(subscriptionPlanFactory).should().create(mappedData);
  }

  @Test
  void createSubscriptionPlanWhenMapperReturnsNullShouldLetFactoryRejectPayloadTest() {
    CreateSubscriptionPlanDTO input = new CreateSubscriptionPlanDTO();
    given(subscriptionPlanMapper.map(input)).willReturn(null);
    given(subscriptionPlanFactory.create(null))
        .willThrow(
            new InvalidSubscriptionPlanPayloadException("subscription plan payload is required"));

    assertThatThrownBy(() -> subscriptionPlanService.createSubscriptionPlan(input))
        .isInstanceOf(InvalidSubscriptionPlanPayloadException.class)
        .hasMessage("subscription plan payload is required");

    then(subscriptionPlanMapper).should().map(input);
    then(subscriptionPlanFactory).should().create(null);
  }

  @Test
  void createSubscriptionPlanWhenFactoryReturnsPlanWithoutIdShouldThrowIllegalStateExceptionTest() {
    CreateSubscriptionPlanDTO input = new CreateSubscriptionPlanDTO();
    SubscriptionPlanData mappedData = SubscriptionPlanData.builder().build();
    SubscriptionPlan createdSubscriptionPlan = new SubscriptionPlan();
    createdSubscriptionPlan.setId(null);
    given(subscriptionPlanMapper.map(input)).willReturn(mappedData);
    given(subscriptionPlanFactory.create(mappedData)).willReturn(createdSubscriptionPlan);

    assertThatThrownBy(() -> subscriptionPlanService.createSubscriptionPlan(input))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("created subscription plan has no identifier");

    then(subscriptionPlanMapper).should().map(input);
    then(subscriptionPlanFactory).should().create(mappedData);
  }
}
