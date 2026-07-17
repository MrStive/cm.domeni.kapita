package com.domeni.kapita.service.mapper;

import cm.domeni.generated.domeni.kapita.dto.CreateSubscriptionPlanDTO;
import cm.domeni.generated.domeni.kapita.dto.MoneyDTO;
import cm.domeni.generated.domeni.kapita.dto.SubscriptionPlanDTO;
import cm.domeni.generated.domeni.kapita.dto.SubscriptionResponseDTO;
import com.domeni.kapita.domain.payment.PaymentResponse;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlan;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanData;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanId;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
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

  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id")
  @Mapping(target = "status", source = "status")
  @Mapping(target = "durationValue", source = "durationValue")
  @Mapping(target = "durationUnit", source = "durationUnit")
  @Mapping(target = "price", source = "price")
  @Mapping(target = "createdAt", source = "createdAt")
  SubscriptionPlanDTO map(SubscriptionPlan value);

  @Mapping(target = "transactionId", source = "paymentId")
  @Mapping(target = "paymentUrl", source = "paymentUrl")
  SubscriptionResponseDTO map(PaymentResponse paymentResponse);

  default MonetaryAmount map(MoneyDTO value) {
    return Optional.ofNullable(value)
        .map(
            input ->
                Money.of(input.getValue(), Optional.ofNullable(input.getCurrency()).orElse("XAF")))
        .orElse(null);
  }

  default MoneyDTO map(MonetaryAmount value) {
    return Optional.ofNullable(value)
        .map(
            input ->
                new MoneyDTO()
                    .currency(input.getCurrency().getCurrencyCode())
                    .value(input.getNumber().numberValue(BigDecimal.class)))
        .orElse(null);
  }

  default UUID map(SubscriptionPlanId value) {
    return Optional.ofNullable(value).map(SubscriptionPlanId::toUUID).orElse(null);
  }
}
