package com.domeni.kapita.repositories;

import com.domeni.kapita.domain.demo.Demo;
import com.domeni.kapita.domain.demo.DemoId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemoSpringRepository extends JpaRepository<Demo, DemoId> {}
