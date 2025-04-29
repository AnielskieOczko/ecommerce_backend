package com.rj.ecommerce_backend.notification;

import com.rj.ecommerce_backend.messaging.email.contract.v1.EmailStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationService {

    private final EmailNotificationRepository emailNotificationRepository;

    /**
     * Record a new email notification
     */
    @Transactional
    public EmailNotification recordEmailSent(String messageId, String recipient, String subject,
                                             String template, String entityType, String entityId) {
        EmailNotification notification = new EmailNotification();
        notification.setMessageId(messageId);
        notification.setRecipient(recipient);
        notification.setSubject(subject);
        notification.setTemplate(template);
        notification.setEntityType(entityType);
        notification.setEntityId(entityId);
        notification.setStatus(EmailStatus.SENT.name());
        notification.setCreatedAt(LocalDateTime.now());

        return emailNotificationRepository.save(notification);
    }

    /**
     * Update the status of an email notification
     */
    @Transactional
    public void updateEmailStatus(String messageId, String status, String errorMessage) {
        EmailNotification notification = emailNotificationRepository
                .findByMessageId(messageId)
                .orElse(null);

        if (notification == null) {
            log.warn("Received status update for unknown email message: {}", messageId);
            return;
        }

        notification.setStatus(status);
        notification.setErrorMessage(errorMessage);
        notification.setUpdatedAt(LocalDateTime.now());

        emailNotificationRepository.save(notification);

        log.info("Updated email notification status: {} -> {}", messageId, status);
    }

    /**
     * Get all email notifications for an entity
     */
    @Transactional(readOnly = true)
    public List<EmailNotification> getEmailNotificationsForEntity(String entityType, String entityId) {
        return emailNotificationRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }

    /**
     * Get all email notifications for a recipient
     */
    @Transactional(readOnly = true)
    public List<EmailNotification> getEmailNotificationsForRecipient(String recipient) {
        return emailNotificationRepository.findByRecipientOrderByCreatedAtDesc(recipient);
    }

    /**
     * Get all email notifications with a specific status
     */
    @Transactional(readOnly = true)
    public List<EmailNotification> getEmailNotificationsByStatus(String status) {
        return emailNotificationRepository.findByStatus(status);
    }
}