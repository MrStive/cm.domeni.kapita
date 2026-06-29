package com.domeni.kapita.domain.debt.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verifyNoInteractions;

import com.domeni.kapita.domain.debt.Debt;
import com.domeni.kapita.domain.debt.DebtId;
import com.domeni.kapita.domain.debt.DebtRepository;
import com.domeni.kapita.domain.debt.DebtStatus;
import com.domeni.kapita.domain.debt.DebtType;
import com.domeni.kapita.domain.exception.DebtNotFoundException;
import com.domeni.kapita.domain.exception.InvalidDebtPayloadException;
import com.domeni.kapita.domain.transaction.Transaction;
import com.domeni.kapita.domain.transaction.TransactionCategory;
import com.domeni.kapita.domain.transaction.TransactionData;
import com.domeni.kapita.domain.transaction.TransactionFactory;
import com.domeni.kapita.domain.transaction.TransactionType;
import com.domeni.kapita.domain.user.UserId;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DebtUpdaterImplTest {

  @Mock private DebtRepository debtRepository;
  @Mock private TransactionFactory transactionFactory;

  @Test
  void settleShouldMarkDebtAsPaidAndCreateIncomingTransactionTest() {
    DebtUpdaterImpl debtSettler = new DebtUpdaterImpl(debtRepository, transactionFactory);
    UserId currentUserId = new UserId(UUID.randomUUID());
    DebtId debtId = new DebtId(UUID.randomUUID());
    Debt debt =
        Debt.builder()
            .id(debtId)
            .type(DebtType.OWED_TO_ME)
            .counterpartyName("Client A")
            .amount(Money.of(new BigDecimal("5000.00"), "XAF"))
            .dueDate(LocalDate.of(2026, 4, 11))
            .status(DebtStatus.UNPAID)
            .userId(currentUserId)
            .createdAt(LocalDateTime.of(2026, 4, 10, 8, 0))
            .build();
    given(debtRepository.findByIdAndUserId(debtId, currentUserId)).willReturn(Optional.of(debt));
    given(debtRepository.save(debt)).willReturn(debt);
    given(transactionFactory.create(any(TransactionData.class), any(UserId.class)))
        .willReturn(new Transaction());

    Debt result = debtSettler.settle(debtId, currentUserId);

    assertThat(result).isSameAs(debt);
    assertThat(result.getStatus()).isEqualTo(DebtStatus.PAID);
    then(debtRepository).should().save(debt);

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
  void settleShouldCreateExpenseTransactionForPayableDebtTest() {
    DebtUpdaterImpl debtSettler = new DebtUpdaterImpl(debtRepository, transactionFactory);
    UserId currentUserId = new UserId(UUID.randomUUID());
    DebtId debtId = new DebtId(UUID.randomUUID());
    Debt debt =
        Debt.builder()
            .id(debtId)
            .type(DebtType.OWED_BY_ME)
            .counterpartyName("Fournisseur B")
            .amount(Money.of(new BigDecimal("15000.00"), "XAF"))
            .dueDate(LocalDate.of(2026, 4, 12))
            .status(DebtStatus.UNPAID)
            .userId(currentUserId)
            .createdAt(LocalDateTime.of(2026, 4, 10, 8, 0))
            .build();
    given(debtRepository.findByIdAndUserId(debtId, currentUserId)).willReturn(Optional.of(debt));
    given(debtRepository.save(debt)).willReturn(debt);
    given(transactionFactory.create(any(TransactionData.class), any(UserId.class)))
        .willReturn(new Transaction());

    debtSettler.settle(debtId, currentUserId);

    ArgumentCaptor<TransactionData> transactionDataCaptor =
        ArgumentCaptor.forClass(TransactionData.class);
    then(transactionFactory).should().create(transactionDataCaptor.capture(), any(UserId.class));
    assertThat(transactionDataCaptor.getValue().type()).isEqualTo(TransactionType.EXPENSE);
  }

  @Test
  void settleWhenDebtIsAlreadyPaidShouldReturnDebtWithoutCreatingTransactionTest() {
    DebtUpdaterImpl debtSettler = new DebtUpdaterImpl(debtRepository, transactionFactory);
    UserId currentUserId = new UserId(UUID.randomUUID());
    DebtId debtId = new DebtId(UUID.randomUUID());
    Debt debt = new Debt();
    debt.setId(debtId);
    debt.setStatus(DebtStatus.PAID);
    given(debtRepository.findByIdAndUserId(debtId, currentUserId)).willReturn(Optional.of(debt));

    Debt result = debtSettler.settle(debtId, currentUserId);

    assertThat(result).isSameAs(debt);
    then(debtRepository).should().findByIdAndUserId(debtId, currentUserId);
    then(debtRepository).shouldHaveNoMoreInteractions();
    verifyNoInteractions(transactionFactory);
  }

  @Test
  void settleWhenDebtDoesNotExistShouldThrowDebtNotFoundExceptionTest() {
    DebtUpdaterImpl debtSettler = new DebtUpdaterImpl(debtRepository, transactionFactory);
    UserId currentUserId = new UserId(UUID.randomUUID());
    DebtId debtId = new DebtId(UUID.randomUUID());
    given(debtRepository.findByIdAndUserId(debtId, currentUserId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> debtSettler.settle(debtId, currentUserId))
        .isInstanceOf(DebtNotFoundException.class)
        .hasMessage("debt not found");
  }

  @Test
  void settleWhenCurrentUserIdIsMissingShouldThrowInvalidDebtPayloadExceptionTest() {
    DebtUpdaterImpl debtSettler = new DebtUpdaterImpl(debtRepository, transactionFactory);
    DebtId debtId = new DebtId(UUID.randomUUID());

    assertThatThrownBy(() -> debtSettler.settle(debtId, null))
        .isInstanceOf(InvalidDebtPayloadException.class)
        .hasMessage("debt user id is required");

    verifyNoInteractions(debtRepository, transactionFactory);
  }
}
