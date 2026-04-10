package com.domeni.kapita.service.mapper;

import cm.domeni.generated.domeni.kapita.dto.CreateDebtDTO;
import cm.domeni.generated.domeni.kapita.dto.MoneyDTO;
import com.domeni.kapita.domain.debt.DebtData;
import java.math.BigDecimal;
import java.util.Optional;
import javax.money.MonetaryAmount;
import org.javamoney.moneta.Money;
import org.mapstruct.BeanMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface DebtMapper {
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "type", source = "type")
  @Mapping(target = "counterpartyName", source = "counterpartyName")
  @Mapping(target = "amount", source = "amount")
  @Mapping(target = "dueDate", source = "dueDate")
  DebtData map(CreateDebtDTO debtDTO);

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
}
