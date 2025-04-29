package com.rj.ecommerce_backend.messaging.email.contract.v1;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Common interface for all ecommerce email requests
 */
public interface EcommerceEmailRequest {
    String getMessageId();
    String getVersion();
    String getTo();
    String getSubject();
    EmailTemplate getTemplate();
    LocalDateTime getTimestamp();

    /**
     * Returns data to be passed to the template engine
     */
    Map<String, Object> getTemplateData();
}
