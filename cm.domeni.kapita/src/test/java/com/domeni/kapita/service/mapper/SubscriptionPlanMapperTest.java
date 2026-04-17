package com.domeni.kapita.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import cm.domeni.generated.domeni.kapita.dto.CreateSubscriptionPlanDTO;
import cm.domeni.generated.domeni.kapita.dto.MoneyDTO;
import cm.domeni.generated.domeni.kapita.dto.SubscriptionPlanDurationUnitDTO;
import cm.domeni.generated.domeni.kapita.dto.SubscriptionPlanStatusDTO;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanData;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanDurationUnit;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanStatus;
import java.math.BigDecimal;
import javax.money.MonetaryAmount;
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
  void mapMoneyDtoShouldReturnMonetaryAmountTest() {
    MonetaryAmount result =
        subscriptionPlanMapper.map(new MoneyDTO().currency("EUR").value(new BigDecimal("49.99")));

    assertThat(result.getCurrency().getCurrencyCode()).isEqualTo("EUR");
    assertThat(result.getNumber().numberValue(BigDecimal.class)).isEqualByComparingTo("49.99");
  }
}
