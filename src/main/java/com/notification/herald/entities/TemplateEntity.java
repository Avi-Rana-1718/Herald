package com.notification.herald.entities;

import com.notification.herald.enums.NotifTypeEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.generator.EventType;
import org.hibernate.type.SqlTypes;

@Data
@Entity
@Table(name = "templates")
@NoArgsConstructor
@AllArgsConstructor
public class TemplateEntity {

  @Id
  @Column(name = "template_id", nullable = false, updatable = false, insertable = false)
  @Generated(event = EventType.INSERT)
  UUID templateId;

  @Column(name = "template_name", nullable = false)
  String templateName;

  @Column(name = "content", nullable = false)
  String content;

  @Column(name = "lang_code", nullable = false)
  String langCode;

  @Enumerated(EnumType.STRING)
  @Column(columnDefinition = "notificationType", nullable = false)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  NotifTypeEnum type;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "variables", columnDefinition = "jsonb")
  List<String> variables;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "metadata", columnDefinition = "jsonb")
  Map<String, Object> metadata;

  @Column(name = "version", nullable = false)
  Integer version;

  @Column(name = "is_active", nullable = false)
  Boolean isActive;
}
