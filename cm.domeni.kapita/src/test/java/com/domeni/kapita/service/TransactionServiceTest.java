package com.domeni.kapita.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import cm.domeni.generated.domeni.kapita.dto.CreateTransactionDTO;
import cm.domeni.generated.domeni.kapita.dto.TransactionCategoryDTO;
import cm.domeni.generated.domeni.kapita.dto.TransactionTypeDTO;
import com.domeni.kapita.domain.exception.InvalidTransactionPayloadException;
import com.domeni.kapita.domain.transaction.Transaction;
import com.domeni.kapita.domain.transaction.TransactionCategory;
import com.domeni.kapita.domain.transaction.TransactionData;
import com.domeni.kapita.domain.transaction.TransactionFactory;
import com.domeni.kapita.domain.transaction.TransactionId;
import com.domeni.kapita.domain.transaction.TransactionType;
import com.domeni.kapita.domain.user.UserId;
import com.domeni.kapita.service.mapper.TransactionMapper;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

  @Mock private TransactionFactory transactionFactory;

  @Mock private TransactionMapper transactionMapper;

  @InjectMocks private TransactionService transactionService;

  @Captor private ArgumentCaptor<TransactionData> transactionDataCaptor;
  @Captor private ArgumentCaptor<UserId> userIdCaptor;

  @Test
  void createTransactionShouldDelegateMappedDataToFactoryAndReturnCreatedTransactionIdTest() {
    CreateTransactionDTO input =
        new CreateTransactionDTO()
            .type(TransactionTypeDTO.INCOMING)
            .category(TransactionCategoryDTO.OTHER)
            .otherCategoryDetail("  special bonus ")
            .amount(new BigDecimal("15000.50"))
            .description(" client payment ");
    UserId currentUserId = new UserId(UUID.randomUUID());
    TransactionData mappedData =
        TransactionData.builder()
            .type(TransactionType.INCOMING)
            .category(TransactionCategory.OTHER)
            .otherCategoryDetail("  special bonus ")
            .amount(new BigDecimal("15000.50"))
            .description(" client payment ")
            .build();
    UUID expectedId = UUID.randomUUID();
    Transaction createdTransaction = new Transaction();
    createdTransaction.setId(new TransactionId(expectedId));

    given(transactionMapper.map(input)).willReturn(mappedData);
    given(transactionFactory.create(mappedData, currentUserId)).willReturn(createdTransaction);

    UUID result = transactionService.createTransaction(input, currentUserId);

    assertThat(result).isEqualTo(expectedId);
    then(transactionMapper).should().map(input);
    then(transactionFactory)
        .should()
        .create(transactionDataCaptor.capture(), userIdCaptor.capture());
    assertThat(transactionDataCaptor.getValue()).isSameAs(mappedData);
    assertThat(userIdCaptor.getValue()).isEqualTo(currentUserId);
  }

  @Test
  void createTransactionWhenMapperReturnsNullShouldLetFactoryRejectPayloadTest() {
    CreateTransactionDTO input = new CreateTransactionDTO();
    UserId currentUserId = new UserId(UUID.randomUUID());
    given(transactionMapper.map(input)).willReturn(null);
    given(transactionFactory.create(null, currentUserId))
        .willThrow(new InvalidTransactionPayloadException("transaction payload is required"));

    assertThatThrownBy(() -> transactionService.createTransaction(input, currentUserId))
        .isInstanceOf(InvalidTransactionPayloadException.class)
        .hasMessage("transaction payload is required");

    then(transactionMapper).should().map(input);
    then(transactionFactory).should().create((TransactionData) null, currentUserId);
  }

  @Test
  void
      createTransactionWhenFactoryReturnsTransactionWithoutIdShouldThrowIllegalStateExceptionTest() {
    CreateTransactionDTO input = new CreateTransactionDTO();
    UserId currentUserId = new UserId(UUID.randomUUID());
    TransactionData mappedData = TransactionData.builder().build();
    Transaction createdTransaction = new Transaction();
    createdTransaction.setId(null);
    given(transactionMapper.map(input)).willReturn(mappedData);
    given(transactionFactory.create(mappedData, currentUserId)).willReturn(createdTransaction);

    assertThatThrownBy(() -> transactionService.createTransaction(input, currentUserId))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("created transaction has no identifier");

    then(transactionMapper).should().map(input);
    then(transactionFactory).should().create(mappedData, currentUserId);
  }
}
