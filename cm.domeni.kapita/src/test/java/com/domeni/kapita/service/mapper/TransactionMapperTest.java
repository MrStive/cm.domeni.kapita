package com.domeni.kapita.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import cm.domeni.generated.domeni.kapita.dto.CreateTransactionDTO;
import cm.domeni.generated.domeni.kapita.dto.TransactionCategoryDTO;
import cm.domeni.generated.domeni.kapita.dto.TransactionTypeDTO;
import com.domeni.kapita.domain.transaction.TransactionCategory;
import com.domeni.kapita.domain.transaction.TransactionData;
import com.domeni.kapita.domain.transaction.TransactionType;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class TransactionMapperTest {

  private final TransactionMapper transactionMapper = Mappers.getMapper(TransactionMapper.class);

  @Test
  void mapCreateTransactionDtoShouldReturnTransactionDataTest() {
    CreateTransactionDTO input =
        new CreateTransactionDTO()
            .type(TransactionTypeDTO.EXPENSE)
            .category(TransactionCategoryDTO.TRANSPORT)
            .otherCategoryDetail("precision")
            .amount(new BigDecimal("2500.00"))
            .description("taxi");

    TransactionData result = transactionMapper.map(input);

    assertThat(result).isNotNull();
    assertThat(result.type()).isEqualTo(TransactionType.EXPENSE);
    assertThat(result.category()).isEqualTo(TransactionCategory.TRANSPORT);
    assertThat(result.otherCategoryDetail()).isEqualTo("precision");
    assertThat(result.amount()).isEqualByComparingTo("2500.00");
    assertThat(result.description()).isEqualTo("taxi");
  }
}
