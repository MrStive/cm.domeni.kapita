package com.domeni.kapita.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import cm.domeni.generated.domeni.kapita.dto.CreateTransactionDTO;
import cm.domeni.generated.domeni.kapita.dto.MoneyDTO;
import cm.domeni.generated.domeni.kapita.dto.TransactionCategoryDTO;
import cm.domeni.generated.domeni.kapita.dto.TransactionDTO;
import cm.domeni.generated.domeni.kapita.dto.TransactionPageDTO;
import cm.domeni.generated.domeni.kapita.dto.TransactionTypeDTO;
import com.domeni.kapita.domain.transaction.Transaction;
import com.domeni.kapita.domain.transaction.TransactionCategory;
import com.domeni.kapita.domain.transaction.TransactionData;
import com.domeni.kapita.domain.transaction.TransactionId;
import com.domeni.kapita.domain.transaction.TransactionPage;
import com.domeni.kapita.domain.transaction.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import javax.money.MonetaryAmount;
import org.javamoney.moneta.Money;
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

  @Test
  void mapTransactionShouldReturnTransactionDtoTest() {
    Transaction input =
        Transaction.builder()
            .id(new TransactionId(UUID.randomUUID()))
            .type(TransactionType.INCOMING)
            .category(TransactionCategory.SALE)
            .amount(new BigDecimal("12500.00"))
            .description("shop sale")
            .createdAt(LocalDateTime.of(2026, 4, 13, 15, 30))
            .build();

    TransactionDTO result = transactionMapper.map(input);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(input.getId().toUUID());
    assertThat(result.getType()).isEqualTo(TransactionTypeDTO.INCOMING);
    assertThat(result.getCategory()).isEqualTo(TransactionCategoryDTO.SALE);
    assertThat(result.getAmount()).isEqualByComparingTo("12500.00");
    assertThat(result.getDescription()).isEqualTo("shop sale");
    assertThat(result.getCreatedAt()).isEqualTo(LocalDateTime.of(2026, 4, 13, 15, 30));
  }

  @Test
  void mapTransactionPageShouldReturnTransactionPageDtoTest() {
    Transaction input =
        Transaction.builder()
            .id(new TransactionId(UUID.randomUUID()))
            .type(TransactionType.EXPENSE)
            .category(TransactionCategory.TRANSPORT)
            .amount(new BigDecimal("2500.00"))
            .description("taxi")
            .createdAt(LocalDateTime.of(2026, 4, 13, 15, 30))
            .build();

    TransactionPageDTO result =
        transactionMapper.map(new TransactionPage(List.of(input), 1, 5, 6, 2));

    assertThat(result).isNotNull();
    assertThat(result.getItems()).hasSize(1);
    assertThat(result.getPageNumber()).isEqualTo(1);
    assertThat(result.getPageSize()).isEqualTo(5);
    assertThat(result.getTotalElements()).isEqualTo(6L);
    assertThat(result.getTotalPages()).isEqualTo(2);
  }

  @Test
  void mapMonetaryAmountShouldReturnMoneyDtoTest() {
    MoneyDTO result = transactionMapper.map(Money.of(new BigDecimal("21249.50"), "XAF"));

    assertThat(result).isNotNull();
    assertThat(result.getCurrency()).isEqualTo("XAF");
    assertThat(result.getValue()).isEqualByComparingTo("21249.50");
  }

  @Test
  void mapMoneyDtoShouldReturnMonetaryAmountTest() {
    MonetaryAmount result =
        transactionMapper.map(new MoneyDTO().currency("XAF").value(new BigDecimal("900.00")));

    assertThat(result.getCurrency().getCurrencyCode()).isEqualTo("XAF");
    assertThat(result.getNumber().numberValue(BigDecimal.class)).isEqualByComparingTo("900.00");
  }
}
