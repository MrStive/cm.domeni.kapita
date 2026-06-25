package com.domeni.kapita.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.domeni.kapita.domain.debt.Debt;
import com.domeni.kapita.domain.debt.DebtData;
import com.domeni.kapita.domain.debt.DebtFactory;
import com.domeni.kapita.domain.debt.DebtFetcher;
import com.domeni.kapita.domain.debt.DebtId;
import com.domeni.kapita.domain.debt.DebtPage;
import com.domeni.kapita.domain.debt.DebtType;
import com.domeni.kapita.domain.debt.DebtUpdater;
import com.domeni.kapita.domain.exception.InvalidDebtPayloadException;
import com.domeni.kapita.domain.user.UserId;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DebtServiceTest {

  @Mock private DebtFetcher debtFetcher;
  @Mock private DebtFactory debtFactory;
  @Mock private DebtUpdater debtUpdater;

  @InjectMocks private DebtService debtService;

  @Test
  void createDebtShouldDelegateDataToFactoryAndReturnCreatedDebtIdTest() {
    UserId currentUserId = new UserId(UUID.randomUUID());
    DebtData input = DebtData.builder().build();
    UUID expectedId = UUID.randomUUID();
    Debt createdDebt = new Debt();
    createdDebt.setId(new DebtId(expectedId));

    given(debtFactory.create(input, currentUserId)).willReturn(createdDebt);

    UUID result = debtService.createDebt(input, currentUserId);

    assertThat(result).isEqualTo(expectedId);
    then(debtFactory).should().create(input, currentUserId);
  }

  @Test
  void createDebtWhenFactoryReturnsDebtWithoutIdShouldThrowIllegalStateExceptionTest() {
    UserId currentUserId = new UserId(UUID.randomUUID());
    DebtData input = DebtData.builder().build();
    Debt createdDebt = new Debt();
    createdDebt.setId(null);
    given(debtFactory.create(input, currentUserId)).willReturn(createdDebt);

    assertThatThrownBy(() -> debtService.createDebt(input, currentUserId))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("created debt has no identifier");

    then(debtFactory).should().create(input, currentUserId);
  }

  @Test
  void getDebtsByTypeShouldDelegateToFetcherTest() {
    UserId currentUserId = new UserId(UUID.randomUUID());
    DebtPage domainPage = new DebtPage(java.util.List.of(), 1, 10, 15, 2);

    given(debtFetcher.getByType(DebtType.RECEIVABLE, 1, 10, currentUserId)).willReturn(domainPage);

    DebtPage result = debtService.getDebtsByType(DebtType.RECEIVABLE, 1, 10, currentUserId);

    assertThat(result).isSameAs(domainPage);
    then(debtFetcher).should().getByType(DebtType.RECEIVABLE, 1, 10, currentUserId);
  }

  @Test
  void markDebtAsPaidShouldDelegateToSettlerTest() {
    UUID debtId = UUID.randomUUID();
    UserId currentUserId = new UserId(UUID.randomUUID());
    Debt settledDebt = new Debt();

    given(debtUpdater.settle(new DebtId(debtId), currentUserId)).willReturn(settledDebt);

    Debt result = debtService.markDebtAsPaid(debtId, currentUserId);

    assertThat(result).isSameAs(settledDebt);
    then(debtUpdater).should().settle(new DebtId(debtId), currentUserId);
  }

  @Test
  void markDebtAsPaidWhenDebtIdIsNullShouldThrowInvalidDebtPayloadExceptionTest() {
    UserId currentUserId = new UserId(UUID.randomUUID());

    assertThatThrownBy(() -> debtService.markDebtAsPaid(null, currentUserId))
        .isInstanceOf(InvalidDebtPayloadException.class)
        .hasMessage("debt id is required");
  }
}
