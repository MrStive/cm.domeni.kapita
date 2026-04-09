package com.domeni.kapita.jpa.scanned;

import com.domeni.kapita.domain.core.SoftDeleteJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity(name = "ScannedEntity")
@Table(name = "t_scanned_entity")
public class ScannedEntity extends SoftDeleteJpaEntity<Long> {

  @Id
  @SequenceGenerator(name = "t_scanned_entity_seq", sequenceName = "t_scanned_entity_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "t_scanned_entity_seq")
  @Column(name = "c_id", nullable = false)
  private Long id;

  @Column(name = "c_name", nullable = false)
  private String name;

  protected ScannedEntity() {}

  public ScannedEntity(Long id, String name) {
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
}
