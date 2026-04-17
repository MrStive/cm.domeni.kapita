package com.domeni.kapita.service;

import cm.domeni.generated.domeni.kapita.dto.CreateSubscriptionPlanDTO;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlan;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanFactory;
import com.domeni.kapita.service.mapper.SubscriptionPlanMapper;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubscriptionPlanService {
  private final SubscriptionPlanFactory subscriptionPlanFactory;
  private final SubscriptionPlanMapper subscriptionPlanMapper;

  @Transactional
  public UUID createSubscriptionPlan(CreateSubscriptionPlanDTO data) {
    SubscriptionPlan createdSubscriptionPlan =
        subscriptionPlanFactory.create(subscriptionPlanMapper.map(data));
    if (createdSubscriptionPlan.getId() == null) {
      throw new IllegalStateException("created subscription plan has no identifier");
    }
    return createdSubscriptionPlan.getId().toUUID();
  }
}
