package com.domeni.kapita.kafka.inbound.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_inbox_event")
@SuppressWarnings("JpaDataSourceORMInspection")
public class InboxEvent {

  @Id
  @Column(name = "c_id")
  private String id;

  @Column(name = "c_type", nullable = false)
  private String type;

  @Enumerated(EnumType.STRING)
  @Column(name = "c_status", nullable = false)
  private InboxEventStatus status;

  @Column(name = "c_created_at", nullable = false)
  private LocalDateTime createdAt;

  @Nullable
  @Column(name = "c_processed_at")
  private LocalDateTime processedAt;

  public InboxEvent processed() {
    processedAt = LocalDateTime.now();
    status = InboxEventStatus.PROCESSED;
    return this;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (!(object instanceof InboxEvent inboxEvent)) {
      return false;
    }
    return Objects.equals(id, inboxEvent.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
