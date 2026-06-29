package com.domeni.kapita.domain.debt.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verifyNoInteractions;

import com.domeni.kapita.domain.debt.Debt;
import com.domeni.kapita.domain.debt.DebtPage;
import com.domeni.kapita.domain.debt.DebtRepository;
import com.domeni.kapita.domain.debt.DebtType;
import com.domeni.kapita.domain.exception.InvalidDebtPayloadException;
import com.domeni.kapita.domain.user.UserId;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DebtFetcherImplTest {

  @Mock private DebtRepository debtRepository;

  @Test
  void getByTypeShouldReturnDebtPageForCurrentUserTest() {
    DebtFetcherImpl objectUnderTest = new DebtFetcherImpl(debtRepository);
    UserId currentUserId = new UserId(UUID.randomUUID());
    DebtPage expectedPage = new DebtPage(List.of(new Debt(), new Debt()), 1, 10, 12, 2);

    given(debtRepository.findAllByUserIdAndType(currentUserId, DebtType.OWED_TO_ME, 1, 10))
        .willReturn(expectedPage);

    DebtPage result = objectUnderTest.getByType(DebtType.OWED_TO_ME, 1, 10, currentUserId);

    assertThat(result).isSameAs(expectedPage);
    then(debtRepository).should().findAllByUserIdAndType(currentUserId, DebtType.OWED_TO_ME, 1, 10);
  }

  @Test
  void getByTypeShouldUseDefaultPaginationAndReturnAllTypesWhenTypeIsMissingTest() {
    DebtFetcherImpl objectUnderTest = new DebtFetcherImpl(debtRepository);
    UserId currentUserId = new UserId(UUID.randomUUID());
    DebtPage expectedPage = new DebtPage(List.of(new Debt()), 0, 10, 1, 1);

    given(debtRepository.findAllByUserId(currentUserId, 0, 10)).willReturn(expectedPage);

    DebtPage result = objectUnderTest.getByType(null, null, null, currentUserId);

    assertThat(result).isSameAs(expectedPage);
    then(debtRepository).should().findAllByUserId(currentUserId, 0, 10);
  }

  @Test
  void getByTypeWhenPageNumberIsInvalidShouldThrowInvalidDebtPayloadExceptionTest() {
    DebtFetcherImpl objectUnderTest = new DebtFetcherImpl(debtRepository);

    assertThatThrownBy(
            () ->
                objectUnderTest.getByType(
                    DebtType.OWED_BY_ME, -1, 10, new UserId(UUID.randomUUID())))
        .isInstanceOf(InvalidDebtPayloadException.class)
        .hasMessage("debt page number is invalid");

    verifyNoInteractions(debtRepository);
  }

  @Test
  void getByTypeWhenPageSizeIsInvalidShouldThrowInvalidDebtPayloadExceptionTest() {
    DebtFetcherImpl objectUnderTest = new DebtFetcherImpl(debtRepository);

    assertThatThrownBy(
            () ->
                objectUnderTest.getByType(DebtType.OWED_BY_ME, 0, 0, new UserId(UUID.randomUUID())))
        .isInstanceOf(InvalidDebtPayloadException.class)
        .hasMessage("debt page size is invalid");

    verifyNoInteractions(debtRepository);
  }

  @Test
  void getByTypeWhenCurrentUserIdIsMissingShouldThrowInvalidDebtPayloadExceptionTest() {
    DebtFetcherImpl objectUnderTest = new DebtFetcherImpl(debtRepository);

    assertThatThrownBy(() -> objectUnderTest.getByType(DebtType.OWED_BY_ME, 0, 10, null))
        .isInstanceOf(InvalidDebtPayloadException.class)
        .hasMessage("debt user id is required");

    verifyNoInteractions(debtRepository);
  }
}
