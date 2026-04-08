package com.domeni.kapita.jpa.autoconfigure;

import com.domeni.kapita.jpa.scanned.ScannedEntity;
import com.domeni.kapita.jpa.scanned.ScannedEntityRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackageClasses = ScannedEntity.class)
@EnableKapitaJpaRepositories(
    basePackageClasses = {TestDemoRepository.class, ScannedEntityRepository.class})
class TestJpaApplication {}
