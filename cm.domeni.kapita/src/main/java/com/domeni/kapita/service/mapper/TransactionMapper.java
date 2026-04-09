package com.domeni.kapita.service.mapper;

import cm.domeni.generated.domeni.kapita.dto.CreateTransactionDTO;
import com.domeni.kapita.domain.transaction.TransactionData;
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
}
