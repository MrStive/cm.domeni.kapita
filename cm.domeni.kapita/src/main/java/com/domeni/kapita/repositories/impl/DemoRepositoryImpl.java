package com.domeni.kapita.repositories.impl;

import com.domeni.kapita.domain.demo.Demo;
import com.domeni.kapita.domain.demo.DemoId;
import com.domeni.kapita.domain.demo.DemoRepository;
import com.domeni.kapita.repositories.DemoSpringRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DemoRepositoryImpl implements DemoRepository {
  private final DemoSpringRepository demoSpringRepository;

  @Override
  public Demo save(Demo value) {
    return demoSpringRepository.save(value);
  }

  @Override
  public List<Demo> findAll() {
    return new ArrayList<>(demoSpringRepository.findAll());
  }

  @Override
  public Optional<Demo> loadDemo(DemoId demoId) {
    return demoSpringRepository.findById(demoId);
  }
}
