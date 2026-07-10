package com.notification.herald.repository;

import com.notification.herald.entities.TemplateEntity;
import com.notification.herald.enums.NotifTypeEnum;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateRepository extends JpaRepository<TemplateEntity, UUID> {

  boolean existsByTemplateNameAndTypeAndLangCode(
      String templateName, NotifTypeEnum type, String langCode);

  Optional<TemplateEntity> findByTemplateNameAndTypeAndLangCode(
      String templateName, NotifTypeEnum type, String langCode);
}
