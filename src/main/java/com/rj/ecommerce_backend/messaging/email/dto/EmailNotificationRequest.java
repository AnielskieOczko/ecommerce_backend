package com.rj.ecommerce_backend.messaging.email.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Builder
public record EmailNotificationRequest(
        String to,
        String subject,
        String template,
        Map<String, Object> data
) implements Serializable {

    // Canonical constructor
    public EmailNotificationRequest {
        // Validate and set default for data
        data = data == null ? new HashMap<>() : data;
    }

    // Builder-like constructor for easier instantiation
    @JsonCreator
    public static EmailNotificationRequest create(
            @JsonProperty("to") String to,
            @JsonProperty("subject") String subject,
            @JsonProperty("template") String template,
            @JsonProperty("data") Map<String, Object> data
    ) {
        return new EmailNotificationRequest(to, subject, template, data);
    }

    // Explicit serial version UID
    private static final long serialVersionUID = 1L;
}
