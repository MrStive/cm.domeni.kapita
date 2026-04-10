package com.domeni.kapita.domain.debt;

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
import jakarta.persistence.Transient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.money.MonetaryAmount;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.javamoney.moneta.Money;

@FieldNameConstants
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "t_debt")
public class Debt extends SoftDeleteJpaEntity<DebtId> {

  @EmbeddedId
  @AttributeOverride(name = "value", column = @Column(name = "c_id"))
  private DebtId id = new DebtId();

  @Enumerated(EnumType.STRING)
  @Column(name = "c_type", nullable = false)
  private DebtType type;

  @Column(name = "c_counterparty_name", nullable = false, length = 255)
  private String counterpartyName;

  @Transient private MonetaryAmount amount;

  @Column(name = "c_amount", nullable = false, precision = 19, scale = 2)
  private java.math.BigDecimal amountValue;

  @Column(name = "c_currency", nullable = false, length = 3)
  private String amountCurrency;

  @Column(name = "c_due_date", nullable = false)
  private LocalDate dueDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "c_status", nullable = false)
  private DebtStatus status;

  @Embedded
  @AttributeOverride(name = "value", column = @Column(name = "c_user_id", nullable = false))
  private UserId userId;

  @Column(name = "c_created_at", nullable = false)
  private LocalDateTime createdAt;

  @Builder
  public Debt(
      DebtId id,
      DebtType type,
      String counterpartyName,
      MonetaryAmount amount,
      LocalDate dueDate,
      DebtStatus status,
      UserId userId,
      LocalDateTime createdAt) {
    this.id = id != null ? id : new DebtId();
    this.type = type;
    this.counterpartyName = counterpartyName;
    setAmount(amount);
    this.dueDate = dueDate;
    this.status = status;
    this.userId = userId;
    this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
  }

  public MonetaryAmount getAmount() {
    if (amount == null && amountValue != null && amountCurrency != null) {
      amount = Money.of(amountValue, amountCurrency);
    }
    return amount;
  }

  public void setAmount(MonetaryAmount amount) {
    this.amount = amount;
    if (amount == null) {
      this.amountValue = null;
      this.amountCurrency = null;
      return;
    }
    this.amountValue = amount.getNumber().numberValueExact(java.math.BigDecimal.class);
    this.amountCurrency = amount.getCurrency().getCurrencyCode();
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (!(object instanceof Debt debt)) {
      return false;
    }
    return Objects.equals(id, debt.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
