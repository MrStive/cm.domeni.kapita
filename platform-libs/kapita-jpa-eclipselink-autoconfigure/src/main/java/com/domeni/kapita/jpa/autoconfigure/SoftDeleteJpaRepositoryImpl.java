package com.domeni.kapita.jpa.autoconfigure;

import com.domeni.kapita.domain.core.KapitaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

public class SoftDeleteJpaRepositoryImpl<T extends KapitaEntity<ID>, ID extends Serializable>
    extends SimpleJpaRepository<T, ID> {

  private static final String DELETED_ATTRIBUTE = "deleted";
  private static final String VERSION_ATTRIBUTE = "version";

  private final JpaEntityInformation<T, ?> entityInformation;
  private final EntityManager entityManager;
  private final String tableName;
  private final String idColumnName;
  private final String deletedColumnName;
  private final String versionColumnName;

  public SoftDeleteJpaRepositoryImpl(
      JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
    super(entityInformation, entityManager);
    this.entityInformation = entityInformation;
    this.entityManager = entityManager;

    Class<T> domainClass = entityInformation.getJavaType();
    this.tableName = resolveTableName(domainClass, entityInformation.getEntityName());
    this.idColumnName =
        resolveColumnName(domainClass, entityInformation.getIdAttribute().getName(), true);
    this.deletedColumnName = resolveColumnName(domainClass, DELETED_ATTRIBUTE, true);
    this.versionColumnName = resolveColumnName(domainClass, VERSION_ATTRIBUTE, false);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<T> findById(ID id) {
    if (entityInformation.hasCompositeId()) {
      return super.findById(id).filter(entity -> !entity.isDeleted());
    }

    String idAttributeName = entityInformation.getIdAttribute().getName();
    Specification<T> byId =
        (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(idAttributeName), id);
    return findOne(byId);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean existsById(ID id) {
    return findById(id).isPresent();
  }

  @Override
  @Transactional
  public void delete(T entity) {
    softDelete(entity);
  }

  @Override
  @Transactional
  public void deleteById(ID id) {
    findById(id).ifPresent(this::softDelete);
  }

  @Override
  @Transactional
  public void deleteAllById(Iterable<? extends ID> ids) {
    ids.forEach(this::deleteById);
  }

  @Override
  @Transactional
  public void deleteAll(Iterable<? extends T> entities) {
    entities.forEach(this::softDelete);
  }

  @Override
  @Transactional
  public void deleteAll() {
    findAll().forEach(this::softDelete);
  }

  @Override
  @Transactional
  public void deleteAllInBatch(Iterable<T> entities) {
    if (entityInformation.hasCompositeId()) {
      entities.forEach(this::softDelete);
      entityManager.flush();
      return;
    }

    List<ID> ids = extractIds(entities);
    if (ids.isEmpty()) {
      return;
    }

    bulkSoftDeleteByIds(ids);
  }

  @Override
  @Transactional
  public void deleteAllInBatch() {
    bulkSoftDeleteAll();
  }

  @Override
  @Transactional
  public void deleteAllByIdInBatch(Iterable<ID> ids) {
    if (entityInformation.hasCompositeId()) {
      deleteAllById(ids);
      entityManager.flush();
      return;
    }

    List<ID> nonNullIds = distinctNonNullIds(ids);
    if (nonNullIds.isEmpty()) {
      return;
    }

    bulkSoftDeleteByIds(nonNullIds);
  }

  @Override
  @Transactional
  public long delete(Specification<T> spec) {
    List<T> entities = findAll(spec);
    entities.forEach(this::softDelete);
    entityManager.flush();
    return entities.size();
  }

  private void softDelete(T entity) {
    if (entity.isDeleted()) {
      return;
    }
    entity.markAsDeleted();
    entityManager.merge(entity);
  }

  private void bulkSoftDeleteByIds(List<ID> ids) {
    StringBuilder sql =
        new StringBuilder("update ")
            .append(tableName)
            .append(" set ")
            .append(deletedColumnName)
            .append(" = ?");
    if (versionColumnName != null) {
      sql.append(", ").append(versionColumnName).append(" = ").append(versionColumnName).append(" + 1");
    }
    sql.append(" where ").append(deletedColumnName).append(" = ? and ").append(idColumnName).append(" in (");
    for (int index = 0; index < ids.size(); index++) {
      if (index > 0) {
        sql.append(", ");
      }
      sql.append("?");
    }
    sql.append(")");

    var updateQuery = entityManager.createNativeQuery(sql.toString());
    int parameterIndex = 1;
    updateQuery.setParameter(parameterIndex++, true);
    updateQuery.setParameter(parameterIndex++, false);
    for (ID id : ids) {
      updateQuery.setParameter(parameterIndex++, id);
    }
    updateQuery.executeUpdate();
    entityManager.flush();
    entityManager.clear();
  }

  private void bulkSoftDeleteAll() {
    StringBuilder sql =
        new StringBuilder("update ")
            .append(tableName)
            .append(" set ")
            .append(deletedColumnName)
            .append(" = ?");
    if (versionColumnName != null) {
      sql.append(", ").append(versionColumnName).append(" = ").append(versionColumnName).append(" + 1");
    }
    sql.append(" where ").append(deletedColumnName).append(" = ?");

    entityManager.createNativeQuery(sql.toString()).setParameter(1, true).setParameter(2, false).executeUpdate();
    entityManager.flush();
    entityManager.clear();
  }

  private List<ID> extractIds(Iterable<? extends T> entities) {
    Set<ID> ids = new LinkedHashSet<>();
    for (T entity : entities) {
      if (entity == null) {
        continue;
      }
      ID id = entity.getId();
      if (id != null) {
        ids.add(id);
      }
    }
    return new ArrayList<>(ids);
  }

  private List<ID> distinctNonNullIds(Iterable<ID> ids) {
    Set<ID> nonNullIds = new LinkedHashSet<>();
    for (ID id : ids) {
      if (id != null) {
        nonNullIds.add(id);
      }
    }
    return new ArrayList<>(nonNullIds);
  }

  private static String resolveTableName(Class<?> domainClass, String fallbackEntityName) {
    Table tableAnnotation = domainClass.getAnnotation(Table.class);
    if (tableAnnotation != null && !tableAnnotation.name().isBlank()) {
      return tableAnnotation.name();
    }
    return fallbackEntityName;
  }

  private static String resolveColumnName(
      Class<?> domainClass, String attributeName, boolean fallbackToAttributeName) {
    Field attributeField = findField(domainClass, attributeName);
    if (attributeField != null) {
      Column columnAnnotation = attributeField.getAnnotation(Column.class);
      if (columnAnnotation != null && !columnAnnotation.name().isBlank()) {
        return columnAnnotation.name();
      }
      return attributeField.getName();
    }

    Method getterMethod = findGetter(domainClass, attributeName);
    if (getterMethod != null) {
      Column columnAnnotation = getterMethod.getAnnotation(Column.class);
      if (columnAnnotation != null && !columnAnnotation.name().isBlank()) {
        return columnAnnotation.name();
      }
    }

    return fallbackToAttributeName ? attributeName : null;
  }

  private static Field findField(Class<?> domainClass, String attributeName) {
    Class<?> currentClass = domainClass;
    while (currentClass != null && currentClass != Object.class) {
      try {
        return currentClass.getDeclaredField(attributeName);
      } catch (NoSuchFieldException ignored) {
        currentClass = currentClass.getSuperclass();
      }
    }
    return null;
  }

  private static Method findGetter(Class<?> domainClass, String attributeName) {
    String suffix = attributeName.substring(0, 1).toUpperCase() + attributeName.substring(1);
    String[] candidateNames = new String[] {"get" + suffix, "is" + suffix};
    for (String candidateName : candidateNames) {
      try {
        return domainClass.getMethod(candidateName);
      } catch (NoSuchMethodException ignored) {
        // Continue.
      }
    }
    return null;
  }
}
