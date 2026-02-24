package com.domeni.kapita.domain.core;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import java.io.Serializable;
import org.eclipse.persistence.annotations.AdditionalCriteria;

@MappedSuperclass
@AdditionalCriteria("this.deleted = false")
public abstract class SoftDeleteJpaEntity<ID extends Serializable> extends BaseKapitaEntity<ID> {

  @Version
  @Column(name = "c_version", nullable = false)
  private long version = 0L;

  @Column(name = "c_deleted", nullable = false)
  private boolean deleted = false;

  public long getVersion() {
    return version;
  }

  @Override
  public boolean isDeleted() {
    return deleted;
  }

  @Override
  public void markAsDeleted() {
    this.deleted = true;
  }

  @Override
  public void markAsNotDeleted() {
    this.deleted = false;
  }
}
