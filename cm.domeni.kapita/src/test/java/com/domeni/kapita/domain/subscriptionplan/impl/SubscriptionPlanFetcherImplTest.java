package com.domeni.kapita.domain.subscriptionplan.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlan;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanRepository;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanStatus;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SubscriptionPlanFetcherImplTest {

  @InjectMocks private SubscriptionPlanFetcherImpl objectUnderTest;

  @Mock private SubscriptionPlanRepository subscriptionPlanRepository;

  @Test
  void getAllActiveShouldReturnOnlyActivePlansTest() {
    SubscriptionPlan activePlan = mock(SubscriptionPlan.class);
    given(subscriptionPlanRepository.findAllByStatus(SubscriptionPlanStatus.ACTIVE))
        .willReturn(List.of(activePlan));

    List<SubscriptionPlan> result = objectUnderTest.getAllActive();

    assertThat(result).containsExactly(activePlan);
    then(subscriptionPlanRepository).should().findAllByStatus(SubscriptionPlanStatus.ACTIVE);
  }
}
