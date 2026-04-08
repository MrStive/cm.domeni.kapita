package com.domeni.kapita.jpa.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;

import com.domeni.kapita.jpa.scanned.ScannedEntity;
import com.domeni.kapita.jpa.scanned.ScannedEntityRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.AopTestUtils;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBootTest(
    classes = TestJpaApplication.class,
    properties = {
      "spring.datasource.url=jdbc:h2:mem:kapita-jpa-autoconfigure-it;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false",
      "spring.datasource.driver-class-name=org.h2.Driver",
      "spring.datasource.username=sa",
      "spring.datasource.password=",
      "spring.jpa.show-sql=false",
      "spring.jpa.generate-ddl=true",
      "spring.jpa.properties.eclipselink.weaving=false",
      "spring.jpa.properties.eclipselink.ddl-generation=create-tables",
      "spring.jpa.properties.eclipselink.ddl-generation.output-mode=database",
      "spring.jpa.properties.eclipselink.target-database=org.eclipse.persistence.platform.database.H2Platform",
      "spring.jpa.properties.eclipselink.logging.level=WARNING",
    })
class KapitaEclipseLinkJpaAutoConfigurationIT {

  @Autowired private TestDemoRepository repository;

  @Autowired private ScannedEntityRepository scannedEntityRepository;

  @Autowired private JdbcTemplate jdbcTemplate;

  @Autowired private PlatformTransactionManager transactionManager;

  @BeforeEach
  void resetTable() {
    jdbcTemplate.update("delete from t_test_demo");
    jdbcTemplate.update("delete from t_scanned_entity");
  }

  @Test
  void wiresInfrastructureAndUsesSoftDeleteRepositoryBaseClass() {
    Object target = AopTestUtils.getTargetObject(repository);
    assertThat(target).isInstanceOf(SoftDeleteJpaRepositoryImpl.class);
    assertThat(transactionManager).isNotNull();
  }

  @Test
  void deleteByIdPerformsSoftDeleteAndAdditionalCriteriaFiltersResult() {
    TestDemo saved = repository.save(new TestDemo(null, "first"));

    repository.deleteById(saved.getId());

    assertThat(repository.findById(saved.getId())).isEmpty();
    assertCounts(0L, 1L);
  }

  @Test
  void allDeleteVariantsRemainSoftDelete() {
    TestDemo first = repository.save(new TestDemo(null, "first"));
    TestDemo second = repository.save(new TestDemo(null, "second"));
    TestDemo third = repository.save(new TestDemo(null, "third"));
    TestDemo fourth = repository.save(new TestDemo(null, "fourth"));

    repository.deleteAllInBatch(List.of(first, second));
    assertCounts(2L, 2L);

    repository.deleteAllByIdInBatch(List.of(third.getId()));
    assertCounts(1L, 3L);

    long specDeleted = repository.delete(nameEquals("fourth"));
    assertThat(specDeleted).isEqualTo(1L);
    assertCounts(0L, 4L);

    TestDemo fifth = repository.save(new TestDemo(null, "fifth"));
    assertThat(fifth.getId()).isNotNull();
    assertCounts(1L, 4L);

    repository.deleteAllInBatch();
    assertCounts(0L, 5L);
    assertThat(repository.findAll()).isEmpty();
  }

  @Test
  void scansEntitiesDeclaredThroughEntityScanPackages() {
    ScannedEntity saved = scannedEntityRepository.save(new ScannedEntity(null, "external"));

    assertThat(saved.getId()).isNotNull();
    assertThat(scannedEntityRepository.findById(saved.getId()))
        .get()
        .extracting(ScannedEntity::getName)
        .isEqualTo("external");
  }

  private void assertCounts(long expectedActive, long expectedDeleted) {
    assertThat(countByDeleted(false)).isEqualTo(expectedActive);
    assertThat(countByDeleted(true)).isEqualTo(expectedDeleted);
  }

  private long countByDeleted(boolean deleted) {
    Long count =
        jdbcTemplate.queryForObject(
            "select count(*) from t_test_demo where c_deleted = ?", Long.class, deleted);
    return count == null ? 0L : count;
  }

  private static Specification<TestDemo> nameEquals(String expectedName) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("name"), expectedName);
  }
}
