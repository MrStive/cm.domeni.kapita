package com.domeni.kapita.domain.core;

import java.io.Serializable;

public interface KapitaEntity<ID extends Serializable> {

  ID getId();

  void setId(ID id);

  boolean isDeleted();

  void markAsDeleted();

  void markAsNotDeleted();
}
