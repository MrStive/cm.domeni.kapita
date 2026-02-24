package com.domeni.kapita.jpa.autoconfigure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

interface TestDemoRepository extends JpaRepository<TestDemo, Long>, JpaSpecificationExecutor<TestDemo> {}
