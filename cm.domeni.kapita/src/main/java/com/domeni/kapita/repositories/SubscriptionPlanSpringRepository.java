package com.domeni.kapita.repositories;

import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlan;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanId;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionPlanSpringRepository
    extends JpaRepository<SubscriptionPlan, SubscriptionPlanId> {

  List<SubscriptionPlan> findByStatus(SubscriptionPlanStatus status);
}
