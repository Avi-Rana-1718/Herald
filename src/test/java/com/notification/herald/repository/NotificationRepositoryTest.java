package com.notification.herald.repository;

import com.notification.herald.entities.NotificationEntity;
import com.notification.herald.enums.NotifTypeEnum;
import com.notification.herald.enums.NotificationStatusEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@Import(FlywayAutoConfiguration.class)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.flyway.enabled=true",
        "spring.flyway.validate-on-migrate=false"
})
class NotificationRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Autowired
    NotificationRepository repository;

    @Test
    void findByID_shouldReturnEntity_whenFound() {
        NotificationEntity entity = new NotificationEntity(
                "notif-id-001", "ref-001", NotifTypeEnum.EMAIL, NotificationStatusEnum.REQUESTED, 0
        );
        repository.save(entity);

        NotificationEntity found = repository.findByID("notif-id-001");

        assertThat(found).isNotNull();
        assertThat(found.getNotificationId()).isEqualTo("notif-id-001");
        assertThat(found.getType()).isEqualTo(NotifTypeEnum.EMAIL);
        assertThat(found.getStatus()).isEqualTo(NotificationStatusEnum.REQUESTED);
        assertThat(found.getRetryCount()).isEqualTo(0);
    }

    @Test
    void findByID_shouldReturnNull_whenNotFound() {
        NotificationEntity result = repository.findByID("non-existent-id");

        assertThat(result).isNull();
    }

    @Test
    void findByID_shouldReturnCorrectEntity_whenMultipleExist() {
        NotificationEntity smsEntity = new NotificationEntity("id-sms", "ref-sms", NotifTypeEnum.SMS, NotificationStatusEnum.FAILED, 3);
        NotificationEntity emailEntity = new NotificationEntity("id-email", "ref-email", NotifTypeEnum.EMAIL, NotificationStatusEnum.REQUESTED, 0);
        repository.saveAll(List.of(smsEntity, emailEntity));

        assertThat(repository.findByID("id-sms").getType()).isEqualTo(NotifTypeEnum.SMS);
        assertThat(repository.findByID("id-sms").getStatus()).isEqualTo(NotificationStatusEnum.FAILED);
        assertThat(repository.findByID("id-email").getType()).isEqualTo(NotifTypeEnum.EMAIL);
    }
}
