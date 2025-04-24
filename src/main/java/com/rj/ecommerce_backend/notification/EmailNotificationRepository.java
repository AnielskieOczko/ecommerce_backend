package com.rj.ecommerce_backend.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmailNotificationRepository extends JpaRepository<EmailNotification, Long> {
    Optional<EmailNotification> findByMessageId(String messageId);

    List<EmailNotification> findByEntityTypeAndEntityId(String entityType, String entityId);

    List<EmailNotification> findByRecipientOrderByCreatedAtDesc(String recipient);

    List<EmailNotification> findByStatus(String status);
}
