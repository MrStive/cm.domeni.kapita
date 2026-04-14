package com.domeni.kapita.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import cm.domeni.generated.domeni.kapita.dto.CreateDebtDTO;
import cm.domeni.generated.domeni.kapita.dto.DebtDTO;
import cm.domeni.generated.domeni.kapita.dto.DebtPageDTO;
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
import com.domeni.kapita.service.mapper.DebtMapper;
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
  @Mock private DebtMapper debtMapper;

  @InjectMocks private DebtService debtService;

  @Test
  void createDebtShouldDelegateMappedDataToFactoryAndReturnCreatedDebtIdTest() {
    CreateDebtDTO input = new CreateDebtDTO();
    UserId currentUserId = new UserId(UUID.randomUUID());
    DebtData mappedData = DebtData.builder().build();
    UUID expectedId = UUID.randomUUID();
    Debt createdDebt = new Debt();
    createdDebt.setId(new DebtId(expectedId));

    given(debtMapper.map(input)).willReturn(mappedData);
    given(debtFactory.create(mappedData, currentUserId)).willReturn(createdDebt);

    UUID result = debtService.createDebt(input, currentUserId);

    assertThat(result).isEqualTo(expectedId);
    then(debtMapper).should().map(input);
    then(debtFactory).should().create(mappedData, currentUserId);
  }

  @Test
  void createDebtWhenMapperReturnsNullShouldLetFactoryRejectPayloadTest() {
    CreateDebtDTO input = new CreateDebtDTO();
    UserId currentUserId = new UserId(UUID.randomUUID());
    given(debtMapper.map(input)).willReturn(null);
    given(debtFactory.create(null, currentUserId))
        .willThrow(new InvalidDebtPayloadException("debt payload is required"));

    assertThatThrownBy(() -> debtService.createDebt(input, currentUserId))
        .isInstanceOf(InvalidDebtPayloadException.class)
        .hasMessage("debt payload is required");

    then(debtMapper).should().map(input);
    then(debtFactory).should().create(null, currentUserId);
  }

  @Test
  void createDebtWhenFactoryReturnsDebtWithoutIdShouldThrowIllegalStateExceptionTest() {
    CreateDebtDTO input = new CreateDebtDTO();
    UserId currentUserId = new UserId(UUID.randomUUID());
    DebtData mappedData = DebtData.builder().build();
    Debt createdDebt = new Debt();
    createdDebt.setId(null);
    given(debtMapper.map(input)).willReturn(mappedData);
    given(debtFactory.create(mappedData, currentUserId)).willReturn(createdDebt);

    assertThatThrownBy(() -> debtService.createDebt(input, currentUserId))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("created debt has no identifier");

    then(debtMapper).should().map(input);
    then(debtFactory).should().create(mappedData, currentUserId);
  }

  @Test
  void getDebtsByTypeShouldDelegateToFetcherAndMapperTest() {
    UserId currentUserId = new UserId(UUID.randomUUID());
    DebtPage domainPage = new DebtPage(java.util.List.of(), 1, 10, 15, 2);
    DebtPageDTO expectedDto =
        new DebtPageDTO().pageNumber(1).pageSize(10).totalElements(15L).totalPages(2);

    given(debtFetcher.getByType(DebtType.RECEIVABLE, 1, 10, currentUserId)).willReturn(domainPage);
    given(debtMapper.map(domainPage)).willReturn(expectedDto);

    DebtPageDTO result = debtService.getDebtsByType(DebtType.RECEIVABLE, 1, 10, currentUserId);

    assertThat(result).isSameAs(expectedDto);
    then(debtFetcher).should().getByType(DebtType.RECEIVABLE, 1, 10, currentUserId);
    then(debtMapper).should().map(domainPage);
  }

  @Test
  void getDebtsByTypeShouldSupportMissingFilterAndPaginationTest() {
    UserId currentUserId = new UserId(UUID.randomUUID());
    DebtPage domainPage = new DebtPage(java.util.List.of(), 0, 10, 0, 0);
    DebtPageDTO expectedDto =
        new DebtPageDTO().pageNumber(0).pageSize(10).totalElements(0L).totalPages(0);

    given(debtFetcher.getByType(null, null, null, currentUserId)).willReturn(domainPage);
    given(debtMapper.map(domainPage)).willReturn(expectedDto);

    DebtPageDTO result = debtService.getDebtsByType(null, null, null, currentUserId);

    assertThat(result).isSameAs(expectedDto);
    then(debtFetcher).should().getByType(null, null, null, currentUserId);
    then(debtMapper).should().map(domainPage);
  }

  @Test
  void markDebtAsPaidShouldDelegateToSettlerAndMapperTest() {
    UUID debtId = UUID.randomUUID();
    UserId currentUserId = new UserId(UUID.randomUUID());
    Debt settledDebt = new Debt();
    DebtDTO expectedDto = new DebtDTO();

    given(debtUpdater.settle(new DebtId(debtId), currentUserId)).willReturn(settledDebt);
    given(debtMapper.map(settledDebt)).willReturn(expectedDto);

    DebtDTO result = debtService.markDebtAsPaid(debtId, currentUserId);

    assertThat(result).isSameAs(expectedDto);
    then(debtUpdater).should().settle(new DebtId(debtId), currentUserId);
    then(debtMapper).should().map(settledDebt);
  }

  @Test
  void markDebtAsPaidWhenDebtIdIsNullShouldThrowInvalidDebtPayloadExceptionTest() {
    UserId currentUserId = new UserId(UUID.randomUUID());

    assertThatThrownBy(() -> debtService.markDebtAsPaid(null, currentUserId))
        .isInstanceOf(InvalidDebtPayloadException.class)
        .hasMessage("debt id is required");
  }
}
