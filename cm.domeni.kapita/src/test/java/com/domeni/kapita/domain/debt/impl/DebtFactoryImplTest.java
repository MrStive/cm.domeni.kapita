package com.domeni.kapita.domain.debt.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verifyNoInteractions;

import com.domeni.kapita.domain.debt.Debt;
import com.domeni.kapita.domain.debt.DebtData;
import com.domeni.kapita.domain.debt.DebtRepository;
import com.domeni.kapita.domain.debt.DebtStatus;
import com.domeni.kapita.domain.debt.DebtType;
import com.domeni.kapita.domain.exception.InvalidDebtPayloadException;
import com.domeni.kapita.domain.transaction.Transaction;
import com.domeni.kapita.domain.transaction.TransactionCategory;
import com.domeni.kapita.domain.transaction.TransactionData;
import com.domeni.kapita.domain.transaction.TransactionFactory;
import com.domeni.kapita.domain.transaction.TransactionType;
import com.domeni.kapita.domain.user.UserId;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DebtFactoryImplTest {

  @Mock private DebtRepository debtRepository;
  @Mock private TransactionFactory transactionFactory;

  @Test
  void createShouldBuildAndPersistDebtFromDebtDataAndCreateIncomingTransactionTest() {
    Clock fixedClock = Clock.fixed(Instant.parse("2026-04-10T10:15:30Z"), ZoneOffset.UTC);
    DebtFactoryImpl debtFactory =
        new DebtFactoryImpl(debtRepository, transactionFactory, fixedClock);
    UserId currentUserId = new UserId(UUID.randomUUID());
    DebtData input =
        DebtData.builder()
            .type(DebtType.RECEIVABLE)
            .counterpartyName("Client A")
            .amount(Money.of(new BigDecimal("5000.00"), "XAF"))
            .dueDate(LocalDate.of(2026, 4, 11))
            .build();
    Debt persistedDebt = new Debt();
    given(debtRepository.save(any(Debt.class))).willReturn(persistedDebt);
    given(transactionFactory.create(any(TransactionData.class), any(UserId.class)))
        .willReturn(new Transaction());

    Debt result = debtFactory.create(input, currentUserId);

    assertThat(result).isSameAs(persistedDebt);

    ArgumentCaptor<Debt> debtCaptor = ArgumentCaptor.forClass(Debt.class);
    then(debtRepository).should().save(debtCaptor.capture());

    Debt debtToSave = debtCaptor.getValue();
    assertThat(debtToSave.getId()).isNotNull();
    assertThat(debtToSave.getId().toUUID()).isNotNull();
    assertThat(debtToSave.getType()).isEqualTo(DebtType.RECEIVABLE);
    assertThat(debtToSave.getCounterpartyName()).isEqualTo("Client A");
    assertThat(debtToSave.getAmount().getCurrency().getCurrencyCode()).isEqualTo("XAF");
    assertThat(debtToSave.getAmount().getNumber().numberValue(BigDecimal.class))
        .isEqualByComparingTo("5000.00");
    assertThat(debtToSave.getDueDate()).isEqualTo(LocalDate.of(2026, 4, 11));
    assertThat(debtToSave.getStatus()).isEqualTo(DebtStatus.UNPAID);
    assertThat(debtToSave.getUserId()).isEqualTo(currentUserId);
    assertThat(debtToSave.getCreatedAt()).isEqualTo(LocalDateTime.of(2026, 4, 10, 10, 15, 30));

    ArgumentCaptor<TransactionData> transactionDataCaptor =
        ArgumentCaptor.forClass(TransactionData.class);
    then(transactionFactory).should().create(transactionDataCaptor.capture(), any(UserId.class));
    assertThat(transactionDataCaptor.getValue().type()).isEqualTo(TransactionType.INCOMING);
    assertThat(transactionDataCaptor.getValue().category()).isEqualTo(TransactionCategory.OTHER);
    assertThat(transactionDataCaptor.getValue().otherCategoryDetail()).isEqualTo("debt");
    assertThat(transactionDataCaptor.getValue().amount()).isEqualByComparingTo("5000.00");
    assertThat(transactionDataCaptor.getValue().description()).isEqualTo("Client A");
  }

  @Test
  void createShouldNormalizeCounterpartyNameBeforePersistingAndTransactionDescriptionTest() {
    DebtFactoryImpl debtFactory =
        new DebtFactoryImpl(debtRepository, transactionFactory, Clock.systemUTC());
    UserId currentUserId = new UserId(UUID.randomUUID());
    DebtData input =
        DebtData.builder()
            .type(DebtType.PAYABLE)
            .counterpartyName("  Fournisseur B  ")
            .amount(Money.of(new BigDecimal("15000.00"), "XAF"))
            .dueDate(LocalDate.of(2026, 4, 12))
            .build();
    given(debtRepository.save(any(Debt.class))).willReturn(new Debt());
    given(transactionFactory.create(any(TransactionData.class), any(UserId.class)))
        .willReturn(new Transaction());

    debtFactory.create(input, currentUserId);

    ArgumentCaptor<Debt> debtCaptor = ArgumentCaptor.forClass(Debt.class);
    ArgumentCaptor<TransactionData> transactionDataCaptor =
        ArgumentCaptor.forClass(TransactionData.class);
    then(debtRepository).should().save(debtCaptor.capture());
    then(transactionFactory).should().create(transactionDataCaptor.capture(), any(UserId.class));
    assertThat(debtCaptor.getValue().getCounterpartyName()).isEqualTo("Fournisseur B");
    assertThat(transactionDataCaptor.getValue().type()).isEqualTo(TransactionType.EXPENSE);
    assertThat(transactionDataCaptor.getValue().description()).isEqualTo("Fournisseur B");
  }

  @Test
  void createWhenCurrentUserIdIsMissingShouldThrowInvalidDebtPayloadExceptionTest() {
    DebtFactoryImpl debtFactory =
        new DebtFactoryImpl(debtRepository, transactionFactory, Clock.systemUTC());
    DebtData input =
        DebtData.builder()
            .type(DebtType.RECEIVABLE)
            .counterpartyName("Client A")
            .amount(Money.of(new BigDecimal("5000.00"), "XAF"))
            .dueDate(LocalDate.of(2026, 4, 11))
            .build();

    assertThatThrownBy(() -> debtFactory.create(input, null))
        .isInstanceOf(InvalidDebtPayloadException.class)
        .hasMessage("debt user id is required");

    verifyNoInteractions(debtRepository, transactionFactory);
  }
}
