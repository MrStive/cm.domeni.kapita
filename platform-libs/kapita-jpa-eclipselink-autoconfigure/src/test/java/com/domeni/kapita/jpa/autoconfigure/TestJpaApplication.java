package com.domeni.kapita.jpa.autoconfigure;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableKapitaJpaRepositories(basePackageClasses = TestDemoRepository.class)
class TestJpaApplication {}
