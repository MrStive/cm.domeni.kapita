package com.domeni.kapita.domain.demo.impl;

import com.domeni.kapita.domain.demo.Demo;
import com.domeni.kapita.domain.demo.DemoFetcher;
import com.domeni.kapita.domain.demo.DemoId;
import com.domeni.kapita.domain.demo.DemoRepository;
import com.domeni.kapita.domain.exception.DemoNotFoundException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DemoFetcherImpl implements DemoFetcher {

  private final DemoRepository demoRepository;

  @Override
  public List<Demo> loadAllDemos() {
    return demoRepository.findAll();
  }

  @Override
  public Demo getById(UUID demoId) {
    return demoRepository.loadDemo(new DemoId(demoId)).orElseThrow(DemoNotFoundException::new);
  }
}
