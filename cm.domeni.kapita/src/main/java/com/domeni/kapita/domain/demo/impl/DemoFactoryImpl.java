package com.domeni.kapita.domain.demo.impl;

import com.domeni.kapita.domain.demo.Demo;
import com.domeni.kapita.domain.demo.DemoData;
import com.domeni.kapita.domain.demo.DemoFactory;
import com.domeni.kapita.domain.demo.DemoId;
import com.domeni.kapita.domain.demo.DemoName;
import com.domeni.kapita.domain.demo.DemoRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DemoFactoryImpl implements DemoFactory {
  private final DemoRepository demoRepository;

  @Override
  public Demo create(DemoData demoData) {
    return demoRepository.save(
        Demo.builder()
            .id(new DemoId(UUID.randomUUID()))
            .name(new DemoName(demoData.name()))
            .build());
  }
}
