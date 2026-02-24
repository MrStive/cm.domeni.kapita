package com.domeni.kapita.jpa.autoconfigure;

import com.domeni.kapita.domain.core.SoftDeleteJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity(name = "TestDemo")
@Table(name = "t_test_demo")
class TestDemo extends SoftDeleteJpaEntity<Long> {

  @Id
  @SequenceGenerator(name = "t_test_demo_seq", sequenceName = "t_test_demo_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "t_test_demo_seq")
  @Column(name = "c_id", nullable = false)
  private Long id;

  @Column(name = "c_name", nullable = false)
  private String name;

  protected TestDemo() {}

  TestDemo(Long id, String name) {
    this.id = id;
    this.name = name;
  }

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
