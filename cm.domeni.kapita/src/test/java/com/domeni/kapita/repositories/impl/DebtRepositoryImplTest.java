package com.domeni.kapita.repositories.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import com.domeni.kapita.domain.debt.Debt;
import com.domeni.kapita.repositories.DebtSpringRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
}
