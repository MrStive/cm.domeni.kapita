package com.domeni.kapita.service.mapper;

import cm.domeni.generated.domeni.kapita.dto.CreateSubscriptionPlanDTO;
import cm.domeni.generated.domeni.kapita.dto.MoneyDTO;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanData;
import java.util.Optional;
import javax.money.MonetaryAmount;
import org.javamoney.moneta.Money;
import org.mapstruct.BeanMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface SubscriptionPlanMapper {
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "status", source = "status")
  @Mapping(target = "durationValue", source = "durationValue")
  @Mapping(target = "durationUnit", source = "durationUnit")
  @Mapping(target = "price", source = "price")
  SubscriptionPlanData map(CreateSubscriptionPlanDTO subscriptionPlanDTO);

  default MonetaryAmount map(MoneyDTO value) {
    return Optional.ofNullable(value)
        .map(
            input ->
                Money.of(input.getValue(), Optional.ofNullable(input.getCurrency()).orElse("XAF")))
        .orElse(null);
  }
}
