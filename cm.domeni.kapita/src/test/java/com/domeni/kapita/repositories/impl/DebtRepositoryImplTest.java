package com.domeni.kapita.repositories.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import com.domeni.kapita.domain.debt.Debt;
import com.domeni.kapita.domain.debt.DebtId;
import com.domeni.kapita.domain.debt.DebtPage;
import com.domeni.kapita.domain.debt.DebtType;
import com.domeni.kapita.domain.user.UserId;
import com.domeni.kapita.repositories.DebtSpringRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class DebtRepositoryImplTest {

  @InjectMocks private DebtRepositoryImpl objectUnderTest;

  @Mock private DebtSpringRepository debtSpringRepository;

  @Test
  void saveShouldDelegateToSpringRepositoryTest() {
    Debt debt = mock(Debt.class);
    Debt persistedDebt = mock(Debt.class);
    given(debtSpringRepository.save(debt)).willReturn(persistedDebt);

    Debt result = objectUnderTest.save(debt);

    assertThat(result).isSameAs(persistedDebt);
    then(debtSpringRepository).should().save(debt);
  }

  @Test
  void findByIdAndUserIdShouldDelegateToSpringRepositoryTest() {
    DebtId debtId = new DebtId(UUID.randomUUID());
    UserId userId = new UserId(UUID.randomUUID());
    Debt persistedDebt = mock(Debt.class);
    given(debtSpringRepository.findByIdAndUserId(debtId, userId))
        .willReturn(Optional.of(persistedDebt));

    Optional<Debt> result = objectUnderTest.findByIdAndUserId(debtId, userId);

    assertThat(result).containsSame(persistedDebt);
    then(debtSpringRepository).should().findByIdAndUserId(debtId, userId);
  }

  @Test
  void findAllByUserIdShouldDelegateToSpringRepositoryAndMapPageTest() {
    UserId userId = new UserId(UUID.randomUUID());
    List<Debt> persistedDebts = List.of(mock(Debt.class), mock(Debt.class));
    PageRequest pageable =
        PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, Debt.Fields.createdAt));

    given(debtSpringRepository.findAllByUserId(userId, pageable))
        .willReturn(new PageImpl<>(persistedDebts, pageable, 2));

    DebtPage result = objectUnderTest.findAllByUserId(userId, 0, 10);

    assertThat(result.items()).containsExactlyElementsOf(persistedDebts);
    assertThat(result.pageNumber()).isEqualTo(0);
    assertThat(result.pageSize()).isEqualTo(10);
    assertThat(result.totalElements()).isEqualTo(2);
    assertThat(result.totalPages()).isEqualTo(1);
    then(debtSpringRepository).should().findAllByUserId(userId, pageable);
  }

  @Test
  void findAllByUserIdAndTypeShouldDelegateToSpringRepositoryAndMapPageTest() {
    UserId userId = new UserId(UUID.randomUUID());
    List<Debt> persistedDebts = List.of(mock(Debt.class), mock(Debt.class));
    PageRequest pageable =
        PageRequest.of(1, 5, Sort.by(Sort.Direction.DESC, Debt.Fields.createdAt));

    given(debtSpringRepository.findAllByUserIdAndType(userId, DebtType.PAYABLE, pageable))
        .willReturn(new PageImpl<>(persistedDebts, pageable, 7));

    DebtPage result = objectUnderTest.findAllByUserIdAndType(userId, DebtType.PAYABLE, 1, 5);

    assertThat(result.items()).containsExactlyElementsOf(persistedDebts);
    assertThat(result.pageNumber()).isEqualTo(1);
    assertThat(result.pageSize()).isEqualTo(5);
    assertThat(result.totalElements()).isEqualTo(7);
    assertThat(result.totalPages()).isEqualTo(2);
    then(debtSpringRepository).should().findAllByUserIdAndType(userId, DebtType.PAYABLE, pageable);
  }
}
