package com.domeni.kapita.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import cm.domeni.generated.domeni.kapita.dto.CreateSubscriptionPlanDTO;
import cm.domeni.generated.domeni.kapita.dto.MoneyDTO;
import cm.domeni.generated.domeni.kapita.dto.SubscriptionPlanDTO;
import cm.domeni.generated.domeni.kapita.dto.SubscriptionPlanDurationUnitDTO;
import cm.domeni.generated.domeni.kapita.dto.SubscriptionPlanStatusDTO;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlan;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanData;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanDurationUnit;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanId;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.money.MonetaryAmount;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class SubscriptionPlanMapperTest {

  private final SubscriptionPlanMapper subscriptionPlanMapper =
      Mappers.getMapper(SubscriptionPlanMapper.class);

  @Test
  void mapCreateSubscriptionPlanDtoShouldReturnSubscriptionPlanDataTest() {
    CreateSubscriptionPlanDTO input =
        new CreateSubscriptionPlanDTO()
            .status(SubscriptionPlanStatusDTO.ACTIVE)
            .durationValue(3)
            .durationUnit(SubscriptionPlanDurationUnitDTO.MONTH)
            .price(new MoneyDTO().currency("XAF").value(new BigDecimal("12500.00")));

    SubscriptionPlanData result = subscriptionPlanMapper.map(input);

    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(SubscriptionPlanStatus.ACTIVE);
    assertThat(result.durationValue()).isEqualTo(3);
    assertThat(result.durationUnit()).isEqualTo(SubscriptionPlanDurationUnit.MONTH);
    assertThat(result.price().getCurrency().getCurrencyCode()).isEqualTo("XAF");
    assertThat(result.price().getNumber().numberValue(BigDecimal.class))
        .isEqualByComparingTo("12500.00");
  }

  @Test
  void mapSubscriptionPlanShouldReturnSubscriptionPlanDTOTest() {
    UUID planId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();
    SubscriptionPlan input =
        SubscriptionPlan.builder()
            .id(new SubscriptionPlanId(planId))
            .status(SubscriptionPlanStatus.ACTIVE)
            .durationValue(3)
            .durationUnit(SubscriptionPlanDurationUnit.MONTH)
            .price(Money.of(12500, "XAF"))
            .createdAt(now)
            .build();

    SubscriptionPlanDTO result = subscriptionPlanMapper.map(input);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(planId);
    assertThat(result.getStatus()).isEqualTo(SubscriptionPlanStatusDTO.ACTIVE);
    assertThat(result.getDurationValue()).isEqualTo(3);
    assertThat(result.getDurationUnit()).isEqualTo(SubscriptionPlanDurationUnitDTO.MONTH);
    assertThat(result.getPrice().getCurrency()).isEqualTo("XAF");
    assertThat(result.getPrice().getValue()).isEqualByComparingTo("12500");
    assertThat(result.getCreatedAt()).isEqualTo(now);
  }

  @Test
  void mapMoneyDtoShouldReturnMonetaryAmountTest() {
    MonetaryAmount result =
        subscriptionPlanMapper.map(new MoneyDTO().currency("EUR").value(new BigDecimal("49.99")));

    assertThat(result.getCurrency().getCurrencyCode()).isEqualTo("EUR");
    assertThat(result.getNumber().numberValue(BigDecimal.class)).isEqualByComparingTo("49.99");
  }
}
