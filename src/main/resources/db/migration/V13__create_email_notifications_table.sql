-- For Flyway: V1__create_email_notifications_table.sql
CREATE TABLE email_notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    message_id VARCHAR(255) NOT NULL UNIQUE,
    recipient VARCHAR(255) NOT NULL,
    subject VARCHAR(255),
    template VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50),
    entity_id VARCHAR(255),
    status VARCHAR(50) NOT NULL,
    error_message VARCHAR(1000),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,

    INDEX idx_message_id (message_id),
    INDEX idx_entity (entity_type, entity_id),
    INDEX idx_recipient (recipient),
    INDEX idx_status (status)
);