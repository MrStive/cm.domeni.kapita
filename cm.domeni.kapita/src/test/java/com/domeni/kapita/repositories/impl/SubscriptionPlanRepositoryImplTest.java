package com.domeni.kapita.repositories.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlan;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanStatus;
import com.domeni.kapita.repositories.SubscriptionPlanSpringRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SubscriptionPlanRepositoryImplTest {

  @InjectMocks private SubscriptionPlanRepositoryImpl objectUnderTest;

  @Mock private SubscriptionPlanSpringRepository subscriptionPlanSpringRepository;

  @Test
  void saveShouldDelegateToSpringRepositoryTest() {
    SubscriptionPlan subscriptionPlan = mock(SubscriptionPlan.class);
    SubscriptionPlan persistedSubscriptionPlan = mock(SubscriptionPlan.class);
    given(subscriptionPlanSpringRepository.save(subscriptionPlan))
        .willReturn(persistedSubscriptionPlan);

    SubscriptionPlan result = objectUnderTest.save(subscriptionPlan);

    assertThat(result).isSameAs(persistedSubscriptionPlan);
    then(subscriptionPlanSpringRepository).should().save(subscriptionPlan);
  }

  @Test
  void findAllByStatusShouldDelegateToSpringRepositoryTest() {
    SubscriptionPlan plan = mock(SubscriptionPlan.class);
    given(subscriptionPlanSpringRepository.findByStatus(SubscriptionPlanStatus.ACTIVE))
        .willReturn(List.of(plan));

    List<SubscriptionPlan> result = objectUnderTest.findAllByStatus(SubscriptionPlanStatus.ACTIVE);

    assertThat(result).containsExactly(plan);
    then(subscriptionPlanSpringRepository).should().findByStatus(SubscriptionPlanStatus.ACTIVE);
  }
}
