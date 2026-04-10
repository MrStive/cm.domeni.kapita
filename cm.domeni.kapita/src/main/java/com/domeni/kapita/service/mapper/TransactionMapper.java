package com.domeni.kapita.service.mapper;

import cm.domeni.generated.domeni.kapita.dto.CreateTransactionDTO;
import cm.domeni.generated.domeni.kapita.dto.MoneyDTO;
import com.domeni.kapita.domain.transaction.TransactionData;
import java.math.BigDecimal;
import java.util.Optional;
import javax.money.MonetaryAmount;
import org.javamoney.moneta.Money;
import org.mapstruct.BeanMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface TransactionMapper {
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "type", source = "type")
  @Mapping(target = "category", source = "category")
  @Mapping(target = "otherCategoryDetail", source = "otherCategoryDetail")
  @Mapping(target = "amount", source = "amount")
  @Mapping(target = "description", source = "description")
  TransactionData map(CreateTransactionDTO transactionDTO);

  default MoneyDTO map(MonetaryAmount value) {
    return Optional.ofNullable(value)
        .map(
            input ->
                new MoneyDTO()
                    .currency(input.getCurrency().getCurrencyCode())
                    .value(input.getNumber().numberValue(BigDecimal.class)))
        .orElse(null);
  }

  default MonetaryAmount map(MoneyDTO value) {
    return Optional.ofNullable(value)
        .map(
            input ->
                Money.of(input.getValue(), Optional.ofNullable(input.getCurrency()).orElse("XAF")))
        .orElse(null);
  }
}
