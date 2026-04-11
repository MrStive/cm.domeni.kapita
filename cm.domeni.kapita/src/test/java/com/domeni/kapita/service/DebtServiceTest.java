package com.domeni.kapita.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import cm.domeni.generated.domeni.kapita.dto.CreateDebtDTO;
import cm.domeni.generated.domeni.kapita.dto.DebtPageDTO;
import com.domeni.kapita.domain.debt.Debt;
import com.domeni.kapita.domain.debt.DebtData;
import com.domeni.kapita.domain.debt.DebtFactory;
import com.domeni.kapita.domain.debt.DebtFetcher;
import com.domeni.kapita.domain.debt.DebtId;
import com.domeni.kapita.domain.debt.DebtPage;
import com.domeni.kapita.domain.debt.DebtType;
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
}
