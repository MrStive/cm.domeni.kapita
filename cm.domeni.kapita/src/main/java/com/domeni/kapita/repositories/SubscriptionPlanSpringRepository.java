package com.domeni.kapita.repositories;

import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlan;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionPlanSpringRepository
    extends JpaRepository<SubscriptionPlan, SubscriptionPlanId> {}
