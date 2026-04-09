package com.domeni.kapita.domain.transaction;

import com.domeni.kapita.domain.core.SoftDeleteJpaEntity;
import com.domeni.kapita.domain.user.UserId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@FieldNameConstants
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "t_transaction")
public class Transaction extends SoftDeleteJpaEntity<TransactionId> {

  @EmbeddedId
  @AttributeOverride(name = "value", column = @Column(name = "c_id"))
  private TransactionId id = new TransactionId();

  @Enumerated(EnumType.STRING)
  @Column(name = "c_type", nullable = false)
  private TransactionType type;

  @Enumerated(EnumType.STRING)
  @Column(name = "c_category", nullable = false)
  private TransactionCategory category;

  @Column(name = "c_other_category_detail")
  private String otherCategoryDetail;

  @Column(name = "c_amount", nullable = false, precision = 19, scale = 2)
  private BigDecimal amount;

  @Column(name = "c_description", length = 1000)
  private String description;

  @Embedded
  @AttributeOverride(name = "value", column = @Column(name = "c_user_id", nullable = false))
  private UserId userId;

  @Column(name = "c_created_at", nullable = false)
  private LocalDateTime createdAt;

  @Builder
  public Transaction(
      TransactionId id,
      TransactionType type,
      TransactionCategory category,
      String otherCategoryDetail,
      BigDecimal amount,
      String description,
      UserId userId,
      LocalDateTime createdAt) {
    this.id = id != null ? id : new TransactionId();
    this.type = type;
    this.category = category;
    this.otherCategoryDetail = otherCategoryDetail;
    this.amount = amount;
    this.description = description;
    this.userId = userId;
    this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (!(object instanceof Transaction transaction)) {
      return false;
    }
    return Objects.equals(id, transaction.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
