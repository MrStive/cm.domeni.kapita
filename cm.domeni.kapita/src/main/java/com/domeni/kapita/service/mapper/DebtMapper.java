package com.domeni.kapita.service.mapper;

import cm.domeni.generated.domeni.kapita.dto.CreateDebtDTO;
import cm.domeni.generated.domeni.kapita.dto.DebtDTO;
import cm.domeni.generated.domeni.kapita.dto.DebtPageDTO;
import cm.domeni.generated.domeni.kapita.dto.DebtTypeDTO;
import cm.domeni.generated.domeni.kapita.dto.MoneyDTO;
import com.domeni.kapita.domain.debt.Debt;
import com.domeni.kapita.domain.debt.DebtData;
import com.domeni.kapita.domain.debt.DebtId;
import com.domeni.kapita.domain.debt.DebtPage;
import com.domeni.kapita.domain.debt.DebtType;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import javax.money.MonetaryAmount;
import org.javamoney.moneta.Money;
import org.mapstruct.BeanMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ValueMapping;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface DebtMapper {
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "type", source = "type")
  @Mapping(target = "counterpartyName", source = "counterpartyName")
  @Mapping(target = "amount", source = "amount")
  @Mapping(target = "dueDate", source = "dueDate")
  DebtData map(CreateDebtDTO debtDTO);

  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id")
  @Mapping(target = "type", source = "type")
  @Mapping(target = "counterpartyName", source = "counterpartyName")
  @Mapping(target = "amount", source = "amount")
  @Mapping(target = "dueDate", source = "dueDate")
  @Mapping(target = "status", source = "status")
  @Mapping(target = "createdAt", source = "createdAt")
  DebtDTO map(Debt value);

  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "items", source = "items")
  @Mapping(target = "pageNumber", source = "pageNumber")
  @Mapping(target = "pageSize", source = "pageSize")
  @Mapping(target = "totalElements", source = "totalElements")
  @Mapping(target = "totalPages", source = "totalPages")
  DebtPageDTO map(DebtPage value);

  @ValueMapping(source = "PAYABLE", target = "PAYABLE")
  @ValueMapping(source = "RECEIVABLE", target = "RECEIVABLE")
  DebtType map(DebtTypeDTO type);

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

  default UUID map(DebtId value) {
    return Optional.ofNullable(value).map(DebtId::toUUID).orElse(null);
  }
}
