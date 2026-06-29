package com.domeni.kapita.service.mapper;

import cm.domeni.generated.domeni.kapita.dto.CreateTransactionDTO;
import cm.domeni.generated.domeni.kapita.dto.MoneyDTO;
import cm.domeni.generated.domeni.kapita.dto.TransactionAmountGroupedDTO;
import cm.domeni.generated.domeni.kapita.dto.TransactionDTO;
import cm.domeni.generated.domeni.kapita.dto.TransactionPageDTO;
import cm.domeni.generated.domeni.kapita.dto.TransactionTypeDTO;
import com.domeni.kapita.domain.transaction.Transaction;
import com.domeni.kapita.domain.transaction.TransactionData;
import com.domeni.kapita.domain.transaction.TransactionId;
import com.domeni.kapita.domain.transaction.TransactionPage;
import com.domeni.kapita.domain.transaction.TransactionType;
import java.math.BigDecimal;
import java.util.Map;
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
public interface TransactionMapper {
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "type", source = "type")
  @Mapping(target = "category", source = "category")
  @Mapping(target = "otherCategoryDetail", source = "otherCategoryDetail")
  @Mapping(target = "amount", source = "amount")
  @Mapping(target = "description", source = "description")
  TransactionData map(CreateTransactionDTO transactionDTO);

  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id")
  @Mapping(target = "type", source = "type")
  @Mapping(target = "category", source = "category")
  @Mapping(target = "otherCategoryDetail", source = "otherCategoryDetail")
  @Mapping(target = "amount", source = "amount")
  @Mapping(target = "description", source = "description")
  @Mapping(target = "createdAt", source = "createdAt")
  TransactionDTO map(Transaction value);

  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "items", source = "items")
  @Mapping(target = "pageNumber", source = "pageNumber")
  @Mapping(target = "pageSize", source = "pageSize")
  @Mapping(target = "totalElements", source = "totalElements")
  @Mapping(target = "totalPages", source = "totalPages")
  TransactionPageDTO map(TransactionPage value);

  @ValueMapping(source = "INCOMING", target = "INCOMING")
  @ValueMapping(source = "EXPENSE", target = "EXPENSE")
  TransactionType map(TransactionTypeDTO type);

  default MoneyDTO map(MonetaryAmount value) {
    return Optional.ofNullable(value)
        .map(
            input ->
                new MoneyDTO()
                    .currency(input.getCurrency().getCurrencyCode())
                    .value(input.getNumber().numberValue(BigDecimal.class)))
        .orElse(null);
  }

  default TransactionAmountGroupedDTO map(MonetaryAmount amount, TransactionType type) {
    TransactionAmountGroupedDTO result = new TransactionAmountGroupedDTO();
    if (type == TransactionType.INCOMING) {
      result.setINCOMING(map(amount));
    } else {
      result.setEXPENSE(map(amount));
    }
    return result;
  }

  default TransactionAmountGroupedDTO map(Map<TransactionType, MonetaryAmount> amounts) {
    TransactionAmountGroupedDTO result = new TransactionAmountGroupedDTO();
    if (amounts.containsKey(TransactionType.INCOMING)) {
      result.setINCOMING(map(amounts.get(TransactionType.INCOMING)));
    }
    if (amounts.containsKey(TransactionType.EXPENSE)) {
      result.setEXPENSE(map(amounts.get(TransactionType.EXPENSE)));
    }
    return result;
  }

  default MonetaryAmount map(MoneyDTO value) {
    return Optional.ofNullable(value)
        .map(
            input ->
                Money.of(input.getValue(), Optional.ofNullable(input.getCurrency()).orElse("XAF")))
        .orElse(null);
  }

  default UUID map(TransactionId value) {
    return Optional.ofNullable(value).map(TransactionId::toUUID).orElse(null);
  }
}
