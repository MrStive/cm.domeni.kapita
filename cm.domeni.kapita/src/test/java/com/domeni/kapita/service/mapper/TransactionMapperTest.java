package com.domeni.kapita.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import cm.domeni.generated.domeni.kapita.dto.CreateTransactionDTO;
import cm.domeni.generated.domeni.kapita.dto.MoneyDTO;
import cm.domeni.generated.domeni.kapita.dto.TransactionBalanceDTO;
import cm.domeni.generated.domeni.kapita.dto.TransactionCategoryDTO;
import cm.domeni.generated.domeni.kapita.dto.TransactionTypeDTO;
import com.domeni.kapita.domain.transaction.TransactionBalance;
import com.domeni.kapita.domain.transaction.TransactionCategory;
import com.domeni.kapita.domain.transaction.TransactionData;
import com.domeni.kapita.domain.transaction.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;
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
  void mapTransactionBalanceShouldReturnTransactionBalanceDtoWithMoneyDtoTest() {
    TransactionBalance input =
        new TransactionBalance(
            LocalDate.of(2026, 1, 1),
            LocalDate.of(2026, 1, 31),
            Money.of(new BigDecimal("21249.50"), "XAF"));

    TransactionBalanceDTO result = transactionMapper.map(input);

    assertThat(result).isNotNull();
    assertThat(result.getStartDate()).isEqualTo(LocalDate.of(2026, 1, 1));
    assertThat(result.getEndDate()).isEqualTo(LocalDate.of(2026, 1, 31));
    assertThat(result.getBalance().getCurrency()).isEqualTo("XAF");
    assertThat(result.getBalance().getValue()).isEqualByComparingTo("21249.50");
  }

  @Test
  void mapMoneyDtoShouldReturnMonetaryAmountTest() {
    MonetaryAmount result =
        transactionMapper.map(new MoneyDTO().currency("XAF").value(new BigDecimal("900.00")));

    assertThat(result.getCurrency().getCurrencyCode()).isEqualTo("XAF");
    assertThat(result.getNumber().numberValue(BigDecimal.class)).isEqualByComparingTo("900.00");
  }
}
