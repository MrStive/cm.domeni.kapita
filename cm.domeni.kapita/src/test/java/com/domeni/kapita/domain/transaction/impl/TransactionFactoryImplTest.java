package com.domeni.kapita.domain.transaction.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verifyNoInteractions;

import com.domeni.kapita.domain.exception.InvalidTransactionPayloadException;
import com.domeni.kapita.domain.transaction.Transaction;
import com.domeni.kapita.domain.transaction.TransactionCategory;
import com.domeni.kapita.domain.transaction.TransactionData;
import com.domeni.kapita.domain.transaction.TransactionRepository;
import com.domeni.kapita.domain.transaction.TransactionType;
import com.domeni.kapita.domain.user.UserId;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionFactoryImplTest {

  @Mock private TransactionRepository transactionRepository;

  @Test
  void createShouldBuildAndPersistTransactionFromTransactionDataTest() {
    Clock fixedClock = Clock.fixed(Instant.parse("2026-04-09T10:15:30Z"), ZoneOffset.UTC);
    TransactionFactoryImpl transactionFactory =
        new TransactionFactoryImpl(transactionRepository, fixedClock);
    UserId currentUserId = new UserId(UUID.randomUUID());
    TransactionData input =
        TransactionData.builder()
            .type(TransactionType.EXPENSE)
            .category(TransactionCategory.TRANSPORT)
            .amount(new BigDecimal("2500.00"))
            .description("taxi")
            .build();
    Transaction persistedTransaction = new Transaction();
    given(transactionRepository.save(any(Transaction.class))).willReturn(persistedTransaction);

    Transaction result = transactionFactory.create(input, currentUserId);

    assertThat(result).isSameAs(persistedTransaction);

    ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
    then(transactionRepository).should().save(transactionCaptor.capture());

    Transaction transactionToSave = transactionCaptor.getValue();
    assertThat(transactionToSave.getId()).isNotNull();
    assertThat(transactionToSave.getId().getValue()).isNotBlank();
    assertThat(transactionToSave.getId().toUUID()).isNotNull();
    assertThat(transactionToSave.getType()).isEqualTo(TransactionType.EXPENSE);
    assertThat(transactionToSave.getCategory()).isEqualTo(TransactionCategory.TRANSPORT);
    assertThat(transactionToSave.getAmount()).isEqualByComparingTo("2500.00");
    assertThat(transactionToSave.getDescription()).isEqualTo("taxi");
    assertThat(transactionToSave.getUserId()).isEqualTo(currentUserId);
    assertThat(transactionToSave.getCreatedAt())
        .isEqualTo(LocalDateTime.of(2026, 4, 9, 10, 15, 30));
  }

  @Test
  void createShouldNormalizeOtherCategoryDetailAndDescriptionBeforePersistingTest() {
    Clock fixedClock = Clock.fixed(Instant.parse("2026-04-09T10:15:30Z"), ZoneOffset.UTC);
    TransactionFactoryImpl transactionFactory =
        new TransactionFactoryImpl(transactionRepository, fixedClock);
    UserId currentUserId = new UserId(UUID.randomUUID());
    TransactionData input =
        TransactionData.builder()
            .type(TransactionType.INCOMING)
            .category(TransactionCategory.OTHER)
            .otherCategoryDetail("  mobile payment  ")
            .amount(new BigDecimal("5000.00"))
            .description("  client deposit ")
            .build();
    given(transactionRepository.save(any(Transaction.class))).willReturn(new Transaction());

    transactionFactory.create(input, currentUserId);

    ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
    then(transactionRepository).should().save(transactionCaptor.capture());
    assertThat(transactionCaptor.getValue().getOtherCategoryDetail()).isEqualTo("mobile payment");
    assertThat(transactionCaptor.getValue().getDescription()).isEqualTo("client deposit");
  }

  @Test
  void createWhenAmountIsNotPositiveShouldThrowInvalidTransactionPayloadExceptionTest() {
    TransactionFactoryImpl transactionFactory =
        new TransactionFactoryImpl(transactionRepository, Clock.systemUTC());
    UserId currentUserId = new UserId(UUID.randomUUID());
    TransactionData input =
        TransactionData.builder()
            .type(TransactionType.INCOMING)
            .category(TransactionCategory.SALE)
            .amount(BigDecimal.ZERO)
            .build();

    assertThatThrownBy(() -> transactionFactory.create(input, currentUserId))
        .isInstanceOf(InvalidTransactionPayloadException.class)
        .hasMessage("transaction payload is invalid");

    verifyNoInteractions(transactionRepository);
  }

  @Test
  void createWhenCategoryDoesNotMatchTypeShouldThrowInvalidTransactionPayloadExceptionTest() {
    TransactionFactoryImpl transactionFactory =
        new TransactionFactoryImpl(transactionRepository, Clock.systemUTC());
    UserId currentUserId = new UserId(UUID.randomUUID());
    TransactionData input =
        TransactionData.builder()
            .type(TransactionType.INCOMING)
            .category(TransactionCategory.STOCK)
            .amount(new BigDecimal("1000.00"))
            .build();

    assertThatThrownBy(() -> transactionFactory.create(input, currentUserId))
        .isInstanceOf(InvalidTransactionPayloadException.class)
        .hasMessage("transaction category is invalid for type");

    verifyNoInteractions(transactionRepository);
  }

  @Test
  void createWhenOtherCategoryDetailIsMissingShouldThrowInvalidTransactionPayloadExceptionTest() {
    TransactionFactoryImpl transactionFactory =
        new TransactionFactoryImpl(transactionRepository, Clock.systemUTC());
    UserId currentUserId = new UserId(UUID.randomUUID());
    TransactionData input =
        TransactionData.builder()
            .type(TransactionType.INCOMING)
            .category(TransactionCategory.OTHER)
            .amount(new BigDecimal("3000.00"))
            .build();

    assertThatThrownBy(() -> transactionFactory.create(input, currentUserId))
        .isInstanceOf(InvalidTransactionPayloadException.class)
        .hasMessage("transaction other category detail is required");

    verifyNoInteractions(transactionRepository);
  }

  @Test
  void createWhenCurrentUserIdIsMissingShouldThrowInvalidTransactionPayloadExceptionTest() {
    TransactionFactoryImpl transactionFactory =
        new TransactionFactoryImpl(transactionRepository, Clock.systemUTC());
    TransactionData input =
        TransactionData.builder()
            .type(TransactionType.INCOMING)
            .category(TransactionCategory.SALE)
            .amount(new BigDecimal("3000.00"))
            .build();

    assertThatThrownBy(() -> transactionFactory.create(input, null))
        .isInstanceOf(InvalidTransactionPayloadException.class)
        .hasMessage("transaction user id is required");

    verifyNoInteractions(transactionRepository);
  }
}
