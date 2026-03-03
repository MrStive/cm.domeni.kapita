package com.domeni.kapita.domain.demo.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.domeni.kapita.domain.demo.Demo;
import com.domeni.kapita.domain.demo.DemoId;
import com.domeni.kapita.domain.demo.DemoRepository;
import com.domeni.kapita.domain.exception.DemoNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DemoFetcherImplTest {

  @Mock private DemoRepository demoRepository;

  @InjectMocks private DemoFetcherImpl demoFetcher;

  @Test
  void loadAllDemosShouldReturnRepositoryValuesTest() {
    // Given
    Demo firstDemo = new Demo();
    Demo secondDemo = new Demo();
    given(demoRepository.findAll()).willReturn(List.of(firstDemo, secondDemo));

    // When
    List<Demo> result = demoFetcher.loadAllDemos();

    // Then
    assertThat(result).containsExactly(firstDemo, secondDemo);
    then(demoRepository).should().findAll();
  }

  @Test
  void getByIdShouldLoadDemoUsingRepositoryAndReturnItTest() {
    // Given
    UUID expectedId = UUID.randomUUID();
    Demo expectedDemo = new Demo();
    given(demoRepository.loadDemo(any(DemoId.class))).willReturn(Optional.of(expectedDemo));

    // When
    Demo result = demoFetcher.getById(expectedId);

    // Then
    assertThat(result).isEqualTo(expectedDemo);

    ArgumentCaptor<DemoId> demoIdCaptor = ArgumentCaptor.forClass(DemoId.class);
    then(demoRepository).should().loadDemo(demoIdCaptor.capture());
    assertThat(demoIdCaptor.getValue().getValue()).isEqualTo(expectedId.toString());
  }

  @Test
  void getByIdWhenDemoDoesNotExistShouldThrowDemoNotFoundExceptionTest() {
    // Given
    UUID expectedId = UUID.randomUUID();
    given(demoRepository.loadDemo(any(DemoId.class))).willReturn(Optional.empty());

    // When / Then
    assertThatThrownBy(() -> demoFetcher.getById(expectedId))
        .isInstanceOf(DemoNotFoundException.class)
        .hasMessage("demo not found");

    ArgumentCaptor<DemoId> demoIdCaptor = ArgumentCaptor.forClass(DemoId.class);
    then(demoRepository).should().loadDemo(demoIdCaptor.capture());
    assertThat(demoIdCaptor.getValue().getValue()).isEqualTo(expectedId.toString());
  }
}
