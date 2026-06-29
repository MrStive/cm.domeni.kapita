package com.domeni.kapita.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import cm.domeni.generated.domeni.kapita.dto.CreateDebtDTO;
import cm.domeni.generated.domeni.kapita.dto.DebtDTO;
import cm.domeni.generated.domeni.kapita.dto.DebtPageDTO;
import cm.domeni.generated.domeni.kapita.dto.DebtStatusDTO;
import cm.domeni.generated.domeni.kapita.dto.DebtTypeDTO;
import cm.domeni.generated.domeni.kapita.dto.MoneyDTO;
import com.domeni.kapita.domain.debt.Debt;
import com.domeni.kapita.domain.debt.DebtData;
import com.domeni.kapita.domain.debt.DebtId;
import com.domeni.kapita.domain.debt.DebtPage;
import com.domeni.kapita.domain.debt.DebtStatus;
import com.domeni.kapita.domain.debt.DebtType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import javax.money.MonetaryAmount;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class DebtMapperTest {

  private final DebtMapper debtMapper = Mappers.getMapper(DebtMapper.class);

  @Test
  void mapCreateDebtDtoShouldReturnDebtDataTest() {
    CreateDebtDTO input =
        new CreateDebtDTO()
            .type(DebtTypeDTO.OWED_TO_ME)
            .counterpartyName("Client A")
            .amount(new MoneyDTO().currency("XAF").value(new BigDecimal("5000.00")))
            .dueDate(LocalDate.of(2026, 4, 11));

    DebtData result = debtMapper.map(input);

    assertThat(result).isNotNull();
    assertThat(result.type()).isEqualTo(DebtType.OWED_TO_ME);
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

  @Test
  void mapDebtShouldReturnDebtDtoTest() {
    Debt input =
        Debt.builder()
            .id(new DebtId(UUID.fromString("6a86c341-c95a-4d7b-ba35-a0836bdb7547")))
            .type(DebtType.OWED_BY_ME)
            .counterpartyName("Fournisseur B")
            .amount(Money.of(new BigDecimal("15000.00"), "XAF"))
            .dueDate(LocalDate.of(2026, 4, 12))
            .status(DebtStatus.UNPAID)
            .createdAt(LocalDateTime.of(2026, 4, 11, 9, 30))
            .build();

    DebtDTO result = debtMapper.map(input);

    assertThat(result.getId()).isEqualTo(UUID.fromString("6a86c341-c95a-4d7b-ba35-a0836bdb7547"));
    assertThat(result.getType()).isEqualTo(DebtTypeDTO.OWED_BY_ME);
    assertThat(result.getCounterpartyName()).isEqualTo("Fournisseur B");
    assertThat(result.getAmount().getCurrency()).isEqualTo("XAF");
    assertThat(result.getAmount().getValue()).isEqualByComparingTo("15000.00");
    assertThat(result.getDueDate()).isEqualTo(LocalDate.of(2026, 4, 12));
    assertThat(result.getStatus()).isEqualTo(DebtStatusDTO.UNPAID);
    assertThat(result.getCreatedAt()).isEqualTo(LocalDateTime.of(2026, 4, 11, 9, 30));
  }

  @Test
  void mapDebtPageShouldReturnDebtPageDtoTest() {
    DebtPage input =
        new DebtPage(
            List.of(
                Debt.builder()
                    .id(new DebtId(UUID.fromString("6a86c341-c95a-4d7b-ba35-a0836bdb7547")))
                    .type(DebtType.OWED_TO_ME)
                    .counterpartyName("Client A")
                    .amount(Money.of(new BigDecimal("5000.00"), "XAF"))
                    .dueDate(LocalDate.of(2026, 4, 11))
                    .status(DebtStatus.UNPAID)
                    .createdAt(LocalDateTime.of(2026, 4, 11, 8, 0))
                    .build()),
            0,
            10,
            1L,
            1);

    DebtPageDTO result = debtMapper.map(input);

    assertThat(result.getItems()).hasSize(1);
    assertThat(result.getPageNumber()).isEqualTo(0);
    assertThat(result.getPageSize()).isEqualTo(10);
    assertThat(result.getTotalElements()).isEqualTo(1L);
    assertThat(result.getTotalPages()).isEqualTo(1);
    assertThat(result.getItems().getFirst().getType()).isEqualTo(DebtTypeDTO.OWED_TO_ME);
  }
}
