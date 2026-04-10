package com.domeni.kapita.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import cm.domeni.generated.domeni.kapita.dto.CreateDebtDTO;
import cm.domeni.generated.domeni.kapita.dto.DebtTypeDTO;
import cm.domeni.generated.domeni.kapita.dto.MoneyDTO;
import com.domeni.kapita.domain.debt.DebtData;
import com.domeni.kapita.domain.debt.DebtType;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.money.MonetaryAmount;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class DebtMapperTest {

  private final DebtMapper debtMapper = Mappers.getMapper(DebtMapper.class);

  @Test
  void mapCreateDebtDtoShouldReturnDebtDataTest() {
    CreateDebtDTO input =
        new CreateDebtDTO()
            .type(DebtTypeDTO.RECEIVABLE)
            .counterpartyName("Client A")
            .amount(new MoneyDTO().currency("XAF").value(new BigDecimal("5000.00")))
            .dueDate(LocalDate.of(2026, 4, 11));

    DebtData result = debtMapper.map(input);

    assertThat(result).isNotNull();
    assertThat(result.type()).isEqualTo(DebtType.RECEIVABLE);
    assertThat(result.counterpartyName()).isEqualTo("Client A");
    assertThat(result.amount().getCurrency().getCurrencyCode()).isEqualTo("XAF");
    assertThat(result.amount().getNumber().numberValue(BigDecimal.class))
        .isEqualByComparingTo("5000.00");
    assertThat(result.dueDate()).isEqualTo(LocalDate.of(2026, 4, 11));
  }

  @Test
  void mapMoneyDtoShouldReturnMonetaryAmountTest() {
    MonetaryAmount result =
        debtMapper.map(new MoneyDTO().currency("XAF").value(new BigDecimal("900.00")));

    assertThat(result.getCurrency().getCurrencyCode()).isEqualTo("XAF");
    assertThat(result.getNumber().numberValue(BigDecimal.class)).isEqualByComparingTo("900.00");
  }
}
