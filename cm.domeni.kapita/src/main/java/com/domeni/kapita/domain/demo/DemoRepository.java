package com.domeni.kapita.domain.demo;

import java.util.List;
import java.util.Optional;

public interface DemoRepository {

  Demo save(Demo value);

  List<Demo> findAll();

  Optional<Demo> loadDemo(DemoId demoId);
}
